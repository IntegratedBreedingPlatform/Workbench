import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-inventory-pane',
    templateUrl: './inventory-pane.component.html'
})
export class InventoryPaneComponent implements OnInit {

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService) {
    }

    ngOnInit(): void {
    }

}
