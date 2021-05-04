import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmList, GermplasmStudy } from '../../shared/germplasm/model/germplasm.model';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { ParamContext } from '../../shared/service/param.context';
import { GRAPHICAL_QUERIES_URL } from '../../app.constants';
import { DomSanitizer } from '@angular/platform-browser';
import { UrlService } from '../../shared/service/url.service';

@Component({
    selector: 'jhi-observations-pane',
    templateUrl: './observations-pane.component.html'
})
export class ObservationsPaneComponent implements OnInit {

    studies: GermplasmStudy[] = [];
    graphicalQueryUrl: SafeResourceUrl;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                public urlService: UrlService,
                private germplasmService: GermplasmService,
                public germplasmDetailsContext: GermplasmDetailsContext,
                private paramContext: ParamContext,
                private sanitizer: DomSanitizer) {

        const queryParams = '?cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectedProjectId=' + this.paramContext.selectedProjectId
            + '&gid=' + this.germplasmDetailsContext.gid;

        this.graphicalQueryUrl = this.sanitizer.bypassSecurityTrustResourceUrl(GRAPHICAL_QUERIES_URL + queryParams);

    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmStudiesByGid(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.studies = value;
        });
    }

    isClickable(study: GermplasmStudy) {
        return this.paramContext.programUUID === study.programUUID;
    }
}
