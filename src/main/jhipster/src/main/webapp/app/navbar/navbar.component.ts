import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { NavService } from './nav.service';
import { DomSanitizer } from '@angular/platform-browser';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { Program } from '../shared/program/model/program';
import { Principal } from '../shared';
import { ToolService } from '../shared/tool/service/tool.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { JhiAlertService } from 'ng-jhipster';
import { Tool, ToolLink } from '../shared/tool/model/tool.model';
import { LoginService } from '../shared/login/login.service';
import { HELP_NAVIGATION_ASK_FOR_SUPPORT, HELP_NAVIGATION_BAR_ABOUT_BMS, VERSION } from '../app.constants';
import { HelpService } from '../shared/service/help.service';
import { ADD_PROGRAM_PERMISSION, SITE_ADMIN_PERMISSIONS } from '../shared/auth/permissions';
import { ActivatedRoute, Router } from '@angular/router';
import { NavbarMessageEvent } from '../shared/model/navbar-message.event';
import { ProgramService } from '../shared/program/service/program.service';
import { ProgramUsageService } from '../shared/service/program-usage.service';
import { CropParameterService } from '../shared/crop-parameter/service/crop-parameter.service';
import { Location, PopStateEvent } from '@angular/common';
import { CropParameterTypeEnum } from '../shared/crop-parameter/model/crop-parameter-type-enum';
import { CropParameter } from '../shared/crop-parameter/model/crop-parameter';

declare const showReleaseNotes: string;

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: [
        'navbar.scss'
    ],
})
export class NavbarComponent implements OnInit, AfterViewInit {

    SITE_ADMIN_PERMISSIONS = SITE_ADMIN_PERMISSIONS;
    ADD_PROGRAM_PERMISSION = ADD_PROGRAM_PERMISSION;

    version: string;
    program: Program;
    user: any;
    toolLinkSelected: string;

    aboutBMSHelpLink: string;
    askForSupportHelpLink: string;

    @ViewChild('sideNav') sideNav: ElementRef;
    @ViewChild('viewport') viewport: ElementRef;

    treeControl = new FlatTreeControl<FlatNode>((node) => node.level, (node) => node.expandable);
    treeFlattener = new MatTreeFlattener(
        this._transformer,
        (node) => node.level,
        (node) => node.expandable, (node) => node.children
    );
    dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    private readonly SESSION_STORAGE_QUERY_PARAM_KEY = 'bms.queryParams';

    constructor(
        private navService: NavService,
        private principal: Principal,
        private sanitizer: DomSanitizer,
        private toolService: ToolService,
        private jhiAlertService: JhiAlertService,
        private loginService: LoginService,
        private helpService: HelpService,
        private programService: ProgramService,
        private programUsageService: ProgramUsageService,
        private cropParameterService: CropParameterService,
        private router: Router,
        private route: ActivatedRoute,
        private location: Location,
    ) {
        this.version = '';
        // Append a ".0" in BMS version if none is found. The .0 gets truncated from workbench.properties to webpack.common version
        if (VERSION) {
            this.version = VERSION.includes('.') ?  `BMS ${VERSION}` : `BMS ${VERSION}.0`;
        }

        // Get about bms help link url
        this.getHelpLink(this.aboutBMSHelpLink, HELP_NAVIGATION_BAR_ABOUT_BMS)
            .then((response) => this.aboutBMSHelpLink = response);
        // Get ask for support link url
        this.getHelpLink(this.askForSupportHelpLink, HELP_NAVIGATION_ASK_FOR_SUPPORT)
            .then((response) => this.askForSupportHelpLink = response);
    }

    hasChild = (_: number, node: any) => node.expandable;

    _transformer(node: Node, level: number) {
        return {
            expandable: !!node.children && node.children.length > 0,
            name: node.name,
            level,
            link: node.link,
            parent: node.parent
        };
    }

    async ngOnInit() {
        this.user = await this.principal.identity();
        this.myPrograms();

        if (showReleaseNotes) {
            this.router.navigate(['/', { outlets: { popup: 'release-notes-popup' }, }], {
                replaceUrl: false,
                skipLocationChange: true,
                queryParamsHandling: 'merge'
            });
            sessionStorage.removeItem(this.SESSION_STORAGE_QUERY_PARAM_KEY)
            return;
        }

        this.restoreRoute();

        this.location.subscribe((popStateEvent) => {
            this.onPopStateEvent(popStateEvent);
        })
    }

    ngAfterViewInit() {
        this.navService.sideNav = this.sideNav;
    }

    async openTool(url) {
        let authParams = '';
        const cropName = this.program ? this.program.crop : null;
        const programUUID = this.program ? this.program.uniqueID : null;
        if (url.includes('/brapi-sync')) {
            authParams += '?destinationToken=' + JSON.parse(localStorage['bms.xAuthToken']).token
                + '&destination=' + window.location.origin + '/bmsapi/' + cropName + '/brapi/v2'
                + '&silentRefreshRedirectUri=' + window.location.origin
                + '/ibpworkbench/controller/pages/brapi-sync/static/silent-refresh.html'

            await this.cropParameterService.getCropParameter(cropName, programUUID, CropParameterTypeEnum.DEFAULT_BRAPI_SYNC_SOURCE)
                .toPromise()
                .then((cropParameter: CropParameter) => {
                    if (cropParameter.value) {
                        authParams += '&source=' + cropParameter.value;
                    }
                });
        } else {
            const hasParams = url.includes('?');
            const selectedProjectId = this.program ? this.program.id : null;
            authParams += (hasParams ? '&' : '?') + 'cropName=' + cropName
                + '&programUUID=' + programUUID
                + '&selectedProjectId=' + selectedProjectId
                + '&loggedInUserId=' + this.user.id;
        }
        authParams += '&restartApplication';

        this.toolLinkSelected = url

        /*
         * avoids glitch when rendering some tools directly (e.g. manage study)
         * where main window is still visible in iframe content for a second
         */
        this.viewport.nativeElement.contentWindow.location.replace('about:blank');
        setTimeout(() => this.viewport.nativeElement.contentWindow.location.replace(url + authParams), 100);

        this.router.navigate(['.'], {
            queryParams: {
                cropName,
                programUUID,
                toolUrl: url,
            }
        });
    }

    myPrograms() {
        this.program = null;
        this.toolLinkSelected = null;
        this.router.navigate(['']);
        this.viewport.nativeElement.contentWindow.location.replace('/ibpworkbench/main/#programs')
    }

    siteAdmin() {
        this.program = null;
        this.openTool('/ibpworkbench/controller/admin');
    }

    about() {
        this.openTool('/ibpworkbench/controller/jhipster#about')
    }

    isSideNavAvailable() {
        return Boolean(this.program);
    }

    @HostListener('window:message', ['$event'])
    async onMessage(event: {data: NavbarMessageEvent}) {
        if (!event.data) {
            return;
        }
        if (event.data.programSelected) {
            const program = event.data.programSelected;
            try {
                await this.programUsageService.save(program.crop, program.uniqueID).toPromise();
                await this.programUsageService.setContextProgram(program.uniqueID).toPromise();
                await this.getTools(program);

                // Open program with specific tool (e.g a particular study in manage studies)
                if (event.data.toolSelected) {
                    this.openTool(event.data.toolSelected);
                    this.expandParent();
                } else {
                    const firstNode = this.treeControl.dataNodes[0];
                    this.treeControl.expand(firstNode);
                    this.openTool(this.treeControl.getDescendants(firstNode)[0].link);
                }
            } catch (error) {
                this.onError(error);
            }
        } else if (event.data.programUpdated && this.program) {
            this.program.name = event.data.programUpdated.name;
        } else if (event.data.programDeleted) {
            this.myPrograms();
        } else if (event.data.toolSelected) {
            this.openTool(event.data.toolSelected);
            this.expandParent();
        } else if (event.data.userProfileChanged) {
            this.user = await this.principal.identity(true)
        }
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
        }
    }

    private toNode(tool: Tool): Node {
        const node: Node = {
            name: tool.name
        };
        node['children'] = tool.children.map((child: ToolLink) => {
            return {
                name: child.name,
                link: child.link,
                parent: node
            }
        });

        return node;
    }

    logout() {
        this.loginService.logout();
    }

    private async getHelpLink(helpLink: string, key: string): Promise<string> {
        if (!helpLink || !helpLink.length) {
            return await this.helpService.getHelpLink(key).toPromise().then((response) => {
                return response.body;
            }).catch((error) => {
                this.onError(error);
            });
        }
    }

    private expandParent() {
        if (!this.treeControl.dataNodes.length) {
            return;
        }
        // Find node by selected tool name
        const selectedNode: FlatNode[] = this.treeControl.dataNodes.filter((node) => this.toolLinkSelected.startsWith(node.link));
        if (selectedNode.length === 1) {
            // Find parent node
            const parentNode: FlatNode[] = this.treeControl.dataNodes.filter((node) => node.name === selectedNode[0].parent.name);
            if (parentNode.length === 1) {
                this.treeControl.expand(parentNode[0]);
            }
        }
    }

    getTools(program) {
        return this.toolService.getTools(program.crop, program.uniqueID)
            .toPromise()
            .then((res: HttpResponse<Tool[]>) => {
                    if (!(res.body && res.body.length)) {
                        this.jhiAlertService.error('error.no.tool.available');
                        return;
                    }

                    this.program = program;
                    this.dataSource.data = res.body.map((response: Tool) => this.toNode(response));
                }, (res: HttpErrorResponse) => this.onError(res)
            );
    }

    openMyProfile() {
        this.router.navigate(['/', { outlets: { popup: 'user-profile-update'}, }], {
            replaceUrl: false,
            skipLocationChange: true,
            queryParamsHandling: 'merge'
        });
    }

    addProgram() {
        this.router.navigate(['/', { outlets: { popup: 'create-program'}, }], {
            replaceUrl: false,
            skipLocationChange: true,
            queryParamsHandling: 'merge'
        });
    }

    @HostListener('window:beforeunload')
    private beforeunload() {
        this.persistQueryParams();
    }

    private persistQueryParams() {
        const queryIndex = this.router.url.lastIndexOf('?');
        if (queryIndex >= 0) {
            sessionStorage.setItem(this.SESSION_STORAGE_QUERY_PARAM_KEY, this.router.url.substring(queryIndex));
        }
    }

    private async restoreRoute() {
        const queryParams = sessionStorage.getItem(this.SESSION_STORAGE_QUERY_PARAM_KEY);
        if (queryParams) {
            await this.router.navigateByUrl(queryParams);
            sessionStorage.removeItem(this.SESSION_STORAGE_QUERY_PARAM_KEY);
        }

        const programUUID = this.route.snapshot.queryParams.programUUID;
        const cropName = this.route.snapshot.queryParams.cropName;
        const toolUrl = this.route.snapshot.queryParams.toolUrl;
        let program: Program;
        if (cropName && programUUID) {
            program = await this.programService.getProgramByProgramUUID(cropName, programUUID).toPromise().then((resp) => resp.body);
        }
        const message: NavbarMessageEvent = {};
        if (program) {
            message.programSelected = program;
        }
        if (toolUrl) {
            message.toolSelected = toolUrl;
        }
        if (Object.keys(message).length) {
            this.onMessage({ data: message });
        }
    }

    private async onPopStateEvent(popStateEvent: PopStateEvent) {
        if (popStateEvent.type === 'popstate') {
            const urlTree = this.router.parseUrl(popStateEvent.url);
            console.log('popstate event: ', popStateEvent.url, urlTree);

            const programUUID = urlTree.queryParams.programUUID;
            const cropName = urlTree.queryParams.cropName;
            const toolUrl = urlTree.queryParams.toolUrl;
            if (cropName && programUUID) {
                this.program = await this.programService.getProgramByProgramUUID(cropName, programUUID).toPromise().then((resp) => resp.body);
            } else {
                this.program = null;
            }
            if (toolUrl) {
                this.toolLinkSelected = toolUrl;
            }

            /*
             * This will override native iframe back/forth functionality.
             * - Fixes Firefox back button, which won't reload iframe contents, though other browsers do (1).
             * - Also fixes back and forward to site admin in all browsers
             * - Fixes back issue to brapi-sync in (1)
             * - in some browsers (1) it will change iframe location twice, but it's almost unnoticeable.
             *
             * (1) - Some browsers iframe history seem to work correctly (e.g. Chrome, Brave)
             */
            if (toolUrl) {
                this.openTool(toolUrl);
            } else {
                this.myPrograms();
            }
        }
    }
}

interface Node {
    name: string;
    children?: Node[];
    link?: string;
    parent?: Node;
}

interface FlatNode {
    expandable: boolean;
    name: string;
    level: number;
    link?: string;
    parent?: Node;
}
