import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { Sample } from '../../entities/sample';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';

@Component({
    selector: 'jhi-samples-pane',
    templateUrl: './samples-pane.component.html'
})
export class SamplesPaneComponent implements OnInit {

    samples: Sample[] = [];

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmService: GermplasmService,
                private germplasmDetailsContext: GermplasmDetailsContext) {
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmSamplesByGid(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.samples = value;
        });
    }

}
