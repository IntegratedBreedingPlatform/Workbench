import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { ParamContext } from '../../shared/service/param.context';
import { EMPTY_PAGE_URL, FILE_MANAGER_URL } from '../../app.constants';
import { DomSanitizer } from '@angular/platform-browser';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'jhi-files-pane',
    templateUrl: './files-pane.component.html'
})
export class FilesPaneComponent implements OnInit {

    safeUrl: SafeResourceUrl = EMPTY_PAGE_URL;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmService: GermplasmService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private paramContext: ParamContext,
                private sanitizer: DomSanitizer,
                private route: ActivatedRoute
    ) {
        this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise().then((resp) => {
            let queryParams = '?cropName=' + paramContext.cropName
                + '&programUUID=' + paramContext.programUUID
                + '&germplasmUUID=' + resp.body.germplasmUUID
                + '&embedded=true';
            const variableName = this.route.snapshot.queryParamMap.get('variableName');
            if (variableName) {
                queryParams += '&variableName=' + variableName;
            }
            this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(FILE_MANAGER_URL + queryParams);
        });

    }

    ngOnInit(): void {
    }

}
