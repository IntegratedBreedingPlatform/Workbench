import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NavService } from './nav.service';
import { DomSanitizer } from '@angular/platform-browser';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { Program } from '../shared/program/model/program';
import { Principal } from '../shared';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    // TODO migrate IBP-4093
    styleUrls: [
        '../../content/css/global-bs4.scss',
        'navbar.scss'
    ],
    encapsulation: ViewEncapsulation.None
})
export class NavbarComponent implements OnInit, AfterViewInit {
    // TODO
    version: string;
    toolUrl: any;
    program: Program;
    user: any;
    toolLinkSelected: string;

    @ViewChild('sideNav') sideNav: ElementRef;

    treeControl = new FlatTreeControl<FlatNode>((node) => node.level, (node) => node.expandable);
    treeFlattener = new MatTreeFlattener(
        this._transformer,
        (node) => node.level,
        (node) => node.expandable, (node) => node.children
    );
    dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    // TODO get from api
    TREE_DATA: Node[] = [{
        name: 'Germplasm',
        children: [{
            name: 'Manage Germplasm',
            link: '/ibpworkbench/controller/jhipster#germplasm-manager'
        }]
    }, {
        name: 'Studies',
        children: [{
            name: 'Single Site Analysis',
            link: '/ibpworkbench/workbenchtools/breeding_view'
        }, {
            name: 'Multi Site Analysis',
            link: '/ibpworkbench/workbenchtools/breeding_gxe'
        }, {
            name: 'Browse studies',
            link: '/ibpworkbench/maingpsb/study/'
        }]
    }, {
        name: 'Program Administration',
        children: [{
            name: 'Manage Program Settings',
            link: '/ibpworkbench/workbenchtools/manage_program'
        }]
    }
    /*, {
        name: 'Sub',
        children: [{
            name: 'sub-sub'
        }, {
            name: 'sub-sub-sub',
            children: [{
                name: 'aaaa'
            }]
        }]
    }*/
    ];

    constructor(
        private navService: NavService,
        private principal: Principal,
        private sanitizer: DomSanitizer,
    ) {
        // TODO
        // this.version = VERSION ? 'v' + VERSION : '';
        this.dataSource.data = this.TREE_DATA;
        this.principal.identity().then((identity) => {
            this.user = identity;
        });
    }

    hasChild = (_: number, node: any) => node.expandable;

    _transformer(node: Node, level: number) {
        return {
            expandable: !!node.children && node.children.length > 0,
            name: node.name,
            level,
            link: node.link
        };
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        this.navService.sideNav = this.sideNav;
    }

    openTool(url) {
        this.toolLinkSelected = url;
        const authParams = '?cropName=' + localStorage['cropName']
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
        this.openTool('/ibpworkbench/controller/admin');
    }

    isSideNavAvailable() {
        return Boolean(this.program);
    }

    @HostListener('window:message', ['$event'])
    onMessage(event) {
        if (!event.data) {
            return;
        }
        if (event.data.programSelected) {
            this.program = event.data.programSelected;
            localStorage['selectedProjectId'] = this.program.id;
            localStorage['loggedInUserId'] = this.user.userId;
            localStorage['cropName'] = this.program.cropName;
            localStorage['programUUID'] = this.program.programUUID;

            const firstNode = this.treeControl.dataNodes[0];
            this.treeControl.expand(firstNode);
            this.openTool(this.treeControl.getDescendants(firstNode)[0].link);
        } else if (event.data.toolSelected) {
            // TODO intra module navigation
        }
    }

}

interface Node {
    name: string;
    children?: Node[];
    link?: string
}

interface FlatNode {
    expandable: boolean;
    name: string;
    level: number;
    link?: string,
}
