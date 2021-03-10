import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-lists-pane',
    templateUrl: './lists-pane.component.html'
})
export class ListsPaneComponent implements OnInit {

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService) {
    }

    ngOnInit(): void {
    }

}
