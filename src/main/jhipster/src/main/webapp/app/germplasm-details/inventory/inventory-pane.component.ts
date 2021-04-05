import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { ParamContext } from '../../shared/service/param.context';
import { INVENTORY_DETAILS_URL } from '../../app.constants';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
    selector: 'jhi-inventory-pane',
    templateUrl: './inventory-pane.component.html'
})
export class InventoryPaneComponent implements OnInit {

    safeUrl: SafeResourceUrl;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private paramContext: ParamContext,
                private sanitizer: DomSanitizer
    ) {

        const queryParams = `?cropName=${paramContext.cropName}&programUUID=${paramContext.programUUID}&gid=${germplasmDetailsContext.gid}`;
        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(INVENTORY_DETAILS_URL + queryParams);

    }

    ngOnInit(): void {
    }

}
