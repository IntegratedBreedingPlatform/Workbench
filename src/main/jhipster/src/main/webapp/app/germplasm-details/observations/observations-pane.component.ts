import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-observations-pane',
    templateUrl: './observations-pane.component.html'
})
export class ObservationsPaneComponent implements OnInit {

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService) {
    }

    ngOnInit(): void {
    }

}
