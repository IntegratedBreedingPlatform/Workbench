import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmDto, GermplasmName } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import { PopupService } from '../../shared/modal/popup.service';
import { Router } from '@angular/router';
import { GermplasmNameContext } from '../../entities/germplasm/name/germplasm-name.context';

@Component({
    selector: 'jhi-basic-details-pane',
    templateUrl: './basic-details-pane.component.html'
})
export class BasicDetailsPaneComponent implements OnInit {

    germplasm: GermplasmDto;
    geojson: any;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmService: GermplasmService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                private popupService: PopupService,
                private router: Router,
                private germplasmNameContext: GermplasmNameContext) {
    }

    ngOnInit(): void {
        this.loadGermplasm();
    }

    loadGermplasm(): void {
        this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasm = value.body;
        })
    }

    editGermplasmName(germplasmName: GermplasmName): void {
        this.germplasmNameContext.germplasmName = germplasmName;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-name-dialog' }, }], {
            queryParamsHandling: 'merge'
        });
    }

    createGermplasmName(): void {
        this.router.navigate(['/', { outlets: { popup: 'germplasm-name-dialog' }, }], {
            queryParamsHandling: 'merge'
        });
    }
}
