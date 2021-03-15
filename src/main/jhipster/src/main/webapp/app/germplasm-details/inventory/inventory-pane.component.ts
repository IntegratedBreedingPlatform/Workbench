import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { LotService } from '../../shared/inventory/service/lot.service';
import { Lot } from '../../shared/inventory/model/lot.model';

@Component({
    selector: 'jhi-inventory-pane',
    templateUrl: './inventory-pane.component.html'
})
export class InventoryPaneComponent implements OnInit {

    lots: Lot[] = [];

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private lotService: LotService) {
    }

    ngOnInit(): void {
        this.lotService.getLotsByGId(this.germplasmDetailsContext.gid, {}).toPromise().then((response) => {
            this.lots = response.body;
        });
    }

}
