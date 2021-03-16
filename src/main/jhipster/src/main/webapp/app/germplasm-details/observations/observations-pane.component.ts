import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmStudy } from '../../shared/germplasm/model/germplasm.model';

@Component({
    selector: 'jhi-observations-pane',
    templateUrl: './observations-pane.component.html'
})
export class ObservationsPaneComponent implements OnInit {

    studies: GermplasmStudy[] = [];

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmService: GermplasmService,
                private germplasmDetailsContext: GermplasmDetailsContext) {
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmStudiesByGid(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.studies = value;
        });
    }

}
