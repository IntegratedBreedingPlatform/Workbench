import { AfterViewInit, Component, ElementRef, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NavService } from './nav.service';
import { DomSanitizer } from '@angular/platform-browser';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { HostListener } from '@angular/core';
import { Program } from '../shared/program/model/program';

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
            link: '/ibpworkbench/main/#/breeding_view'
        }, {
            name: 'Browse studies',
            link: '/ibpworkbench/maingpsb/study/'
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
            + '&authToken=' + localStorage['authToken']
            + '&selectedProjectId=' + localStorage['selectedProjectId']
            + '&loggedInUserId=' + localStorage['loggedInUserId']
            + '&restartApplication';
        this.toolUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url + authParams);
    }

    isSideNavAvailable() {
        return Boolean(this.toolUrl);
    }

    @HostListener('window:message', ['$event'])
    onMessage(event) {
        if (event.data && event.data.programSelected) {
            let program: Program = event.data.programSelected;
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
