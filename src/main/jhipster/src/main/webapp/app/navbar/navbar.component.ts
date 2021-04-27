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
import { UserProgramInfoService } from '../shared/service/user-program-info.service';
import { Router } from '@angular/router';
import { NavbarMessageEvent } from '../shared/model/navbar-message.event';

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
    toolUrl: any;
    program: Program;
    user: any;
    toolLinkSelected: string;

    aboutBMSHelpLink: string;
    askForSupportHelpLink: string;

    @ViewChild('sideNav') sideNav: ElementRef;

    treeControl = new FlatTreeControl<FlatNode>((node) => node.level, (node) => node.expandable);
    treeFlattener = new MatTreeFlattener(
        this._transformer,
        (node) => node.level,
        (node) => node.expandable, (node) => node.children
    );
    dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    constructor(
        private navService: NavService,
        private principal: Principal,
        private sanitizer: DomSanitizer,
        private toolService: ToolService,
        private jhiAlertService: JhiAlertService,
        private loginService: LoginService,
        private helpService: HelpService,
        private userProgramInfoService: UserProgramInfoService,
        private router: Router
    ) {
        this.version = VERSION ? `BMS ${VERSION}` : '';
        this.principal.identity().then((identity) => {
            this.user = identity;
        });

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

    ngOnInit() {
        if (showReleaseNotes) {
            this.router.navigate(['/', { outlets: { popup: 'release-notes-popup' }, }], {
                replaceUrl: false,
                skipLocationChange: true,
                queryParamsHandling: 'merge'
            });
        }
    }

    ngAfterViewInit() {
        this.navService.sideNav = this.sideNav;
    }

    openTool(url) {
        const hasParams = url.includes('?');
        this.toolLinkSelected = url;
        const authParams = (hasParams ? '&' : '?') + 'cropName=' + localStorage['cropName']
            + '&programUUID=' + localStorage['programUUID']
            // Deprecated, not needed
            // + '&authToken=' + localStorage['authToken']
            + '&selectedProjectId=' + localStorage['selectedProjectId']
            + '&loggedInUserId=' + localStorage['loggedInUserId']
            + '&restartApplication';
        this.toolUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url + authParams);
    }

    addProgram() {
        this.openTool('/ibpworkbench/workbenchtools/create_program');
    }

    myPrograms() {
        this.program = null;
        this.toolUrl = '';
    }

    siteAdmin() {
        this.program = null;
        this.openTool('/ibpworkbench/controller/admin');
    }

    about() {
        this.openTool('/ibpworkbench/controller/about/')
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
                await this.userProgramInfoService.setSelectedProgram(program.uniqueID).toPromise();
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
            this.program = null;
            localStorage.removeItem('selectedProjectId');
            localStorage.removeItem('cropName');
            localStorage.removeItem('programUUID');
            window.top.location.href = '/ibpworkbench/main/';
        } else if (event.data.toolSelected) {
            this.openTool(event.data.toolSelected);
            this.expandParent();
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
                    localStorage['selectedProjectId'] = this.program.id;
                    localStorage['loggedInUserId'] = this.user.id;
                    localStorage['cropName'] = this.program.crop;
                    localStorage['programUUID'] = this.program.uniqueID;

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
