import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { Germplasm } from '../../entities/germplasm/germplasm.model';

@Component({
    selector: 'jhi-basic-details-pane',
    templateUrl: './basic-details-pane.component.html'
})
export class BasicDetailsPaneComponent implements OnInit {

    germplasm: Germplasm;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmService: GermplasmService,
                private germplasmDetailsContext: GermplasmDetailsContext) {
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasm = value.body;
            console.log(this.germplasm);
        })
    }

}
