import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { VERSION } from '../app.constants';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-about',
    templateUrl: './about.component.html'
})
export class AboutComponent implements OnInit {

    version: string;

    constructor(private router: Router,
                private languageService: JhiLanguageService
    ) {
        this.version = '';
        // Append a ".0" in version if none is found. The .0 gets truncated from workbench.properties to webpack.common version
        if (VERSION) {
            this.version = VERSION.includes('.') ?  ` v${VERSION}` : ` v${VERSION}.0`;
        }
    }

    ngOnInit(): void {
    }

    openReleaseNote($event): void {
        $event.preventDefault();
        this.router.navigate(['/', { outlets: { popup: 'release-notes-popup' }, }], {
            replaceUrl: false,
            skipLocationChange: true,
            queryParamsHandling: 'merge',
            queryParams:
                {
                    showAgainCheckbox: false
                }
        });
    }
}
