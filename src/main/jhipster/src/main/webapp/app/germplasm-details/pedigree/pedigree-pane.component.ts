import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { PEDIGREE_DETAILS_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { DomSanitizer } from '@angular/platform-browser';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { GermplasmProgenitorsDetails } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import {Router} from "@angular/router";
import {GermplasmProgenitorsContext} from "../../entities/germplasm/progenitors/germplasm-progenitors.context";

@Component({
    selector: 'jhi-pedigree-pane',
    templateUrl: './pedigree-pane.component.html'
})
export class PedigreePaneComponent implements OnInit {

    @ViewChild('pedigreeIframe') pedigreeIframe: ElementRef;

    germplasmProgenitorsDetails: GermplasmProgenitorsDetails;
    safeUrl: SafeResourceUrl;
    isIframeLoaded: boolean;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private paramContext: ParamContext,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private sanitizer: DomSanitizer,
                private germplasmService: GermplasmService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                private router: Router,
                private germplasmProgenitorsContext: GermplasmProgenitorsContext) {
        const authParams = '?gid=' + this.germplasmDetailsContext.gid
            + '&cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&authToken=' + this.paramContext.authToken
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId;

        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(PEDIGREE_DETAILS_URL + authParams);
    }

    onIframeLoad(): void {
        if (this.pedigreeIframe.nativeElement.src) {
            // Only display the iframe after the iframe page is loaded, this is to prevent the page from automatically scrolling down when iframe source is loaded.
            setTimeout(() => {
                this.isIframeLoaded = true;
            }, 1000);
        }
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmProgenitorsDetails(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasmProgenitorsDetails = value;
        });
    }

    editPedigree(progenidtorDetails: GermplasmProgenitorsDetails): void {
        this.germplasmProgenitorsContext.germplasmProgenitorsDetails = progenidtorDetails;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-progenitors-dialog' }, }], {
            queryParamsHandling: 'merge'
        });
    }
}
