import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { PEDIGREE_DETAILS_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { GermplasmProgenitorsDetails } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsUrlService } from '../germplasm-details.url.service';

@Component({
    selector: 'jhi-pedigree-pane',
    templateUrl: './pedigree-pane.component.html'
})
export class PedigreePaneComponent implements OnInit {

    germplasmProgenitorsDetails: GermplasmProgenitorsDetails;
    safeUrl: SafeResourceUrl;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private paramContext: ParamContext,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private sanitizer: DomSanitizer,
                private germplasmService: GermplasmService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService) {
        const authParams = '?gid=' + this.germplasmDetailsContext.gid
            + '&cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&authToken=' + this.paramContext.authToken
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId;

        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(PEDIGREE_DETAILS_URL + authParams);
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmProgenitorsDetails(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasmProgenitorsDetails = value;
        });
    }

}
