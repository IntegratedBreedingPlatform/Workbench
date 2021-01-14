import { AfterViewInit, Component, ElementRef, HostListener, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NavService } from './nav.service';
import { DomSanitizer } from '@angular/platform-browser';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { Program } from '../shared/program/model/program';
import { Principal } from '../shared';

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
        // TODO store params in localStorage in select program window
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

    isSideNavAvailable() {
        return Boolean(this.toolUrl);
    }

    @HostListener('window:message', ['$event'])
    async onMessage(event) {
        const identity = await this.principal.identity();
        if (event.data && event.data.programSelected) {
            const program: Program = event.data.programSelected;
            localStorage['selectedProjectId'] = program.id;
            localStorage['loggedInUserId'] = identity.userId;
            localStorage['cropName'] = program.cropName;
            localStorage['programUUID'] = program.programUUID;
            this.openTool(this.TREE_DATA[0].children[0].link)
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
