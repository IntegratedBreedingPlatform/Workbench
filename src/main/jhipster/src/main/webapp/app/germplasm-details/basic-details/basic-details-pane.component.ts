import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-basic-details-pane',
    templateUrl: './basic-details-pane.component.html'
})
export class BasicDetailsPaneComponent implements OnInit {

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService) {
    }

    ngOnInit(): void {
    }

}
