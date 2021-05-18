import { Component, OnInit } from '@angular/core';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmProgenitorsDetails } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import { ActivatedRoute, Router } from '@angular/router';
import { GermplasmProgenitorsContext } from '../../entities/germplasm/progenitors/germplasm-progenitors.context';
import { Subscription } from 'rxjs';
import { EDIT_GERMPLASM_PERMISSION } from '../../shared/auth/permissions';
import { BreedingMethodTypeEnum } from '../../shared/breeding-method/model/breeding-method-type.model';

@Component({
    selector: 'jhi-pedigree-pane',
    templateUrl: './pedigree-pane.component.html'
})
export class PedigreePaneComponent implements OnInit {

    MODIFY_PEDIGREE_PERMISSIONS = [...EDIT_GERMPLASM_PERMISSION, 'MODIFY_PEDIGREE'];

    eventSubscriber: Subscription;
    germplasmProgenitorsDetails: GermplasmProgenitorsDetails;
    isIframeLoaded: boolean;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                public germplasmDetailsContext: GermplasmDetailsContext,
                private germplasmService: GermplasmService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                private router: Router,
                private activatedRoute: ActivatedRoute,
                private germplasmProgenitorsContext: GermplasmProgenitorsContext) {
    }

    ngOnInit(): void {
        this.registerGermplasmNameChanged();
        this.loadProgenitorDetails();
    }

    registerGermplasmNameChanged() {
        this.eventSubscriber = this.eventManager.subscribe('progenitorsChanged', (event) => {
            this.loadProgenitorDetails();
            this.germplasmDetailsContext.notifyGermplasmDetailChanges();
        });
    }

    loadProgenitorDetails() {
        this.germplasmService.getGermplasmProgenitorsDetails(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasmProgenitorsDetails = value;
        });
    }

    editPedigree(progenitorsDetails: GermplasmProgenitorsDetails): void {
        this.germplasmProgenitorsContext.germplasmProgenitorsDetails = progenitorsDetails;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-progenitors-dialog/' + this.germplasmDetailsContext.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    isGenerative(): boolean {
        return this.germplasmProgenitorsDetails && this.germplasmProgenitorsDetails.breedingMethodType === BreedingMethodTypeEnum.GENERATIVE;
    }

}
