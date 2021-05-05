import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmList } from '../../shared/germplasm/model/germplasm.model';
import { ParamContext } from '../../shared/service/param.context';
import { UrlService } from '../../shared/service/url.service';

@Component({
    selector: 'jhi-lists-pane',
    templateUrl: './lists-pane.component.html'
})
export class ListsPaneComponent implements OnInit {

    germplasmLists: GermplasmList[] = [];

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                public urlService: UrlService,
                private germplasmService: GermplasmService,
                public germplasmDetailsContext: GermplasmDetailsContext,
                public paramContext: ParamContext) {
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmListsByGid(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasmLists = value;
        });
    }

    isClickable(germplasmList: GermplasmList) {
        return this.paramContext.programUUID === germplasmList.programUUID;
    }

}
