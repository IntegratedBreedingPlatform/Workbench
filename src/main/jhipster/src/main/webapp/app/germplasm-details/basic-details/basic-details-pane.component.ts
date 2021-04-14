import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import { Router } from '@angular/router';

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
                public router: Router) {
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasm = value.body;
        })
    }

    editGermplasmBasicDetails(): void {
        this.germplasmDetailsContext.germplasm = this.germplasm;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-edit-basic-details' }, }], {
            queryParamsHandling: 'merge'
        });
    }

}
