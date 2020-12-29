import { Component, OnInit, ViewEncapsulation, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { NavService } from './nav.service';
import { GERMPLASM_BROWSER_DEFAULT_URL } from '../app.constants';
import { DomSanitizer } from '@angular/platform-browser';

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
    safeUrl: any;

    @ViewChild('sideNav') sideNav: ElementRef;

    constructor(
        private navService: NavService,
        private sanitizer: DomSanitizer,
    ) {
        // TODO
        // this.version = VERSION ? 'v' + VERSION : '';
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
        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url + authParams);
    }

}
