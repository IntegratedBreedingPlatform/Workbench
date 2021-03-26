import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmAttribute } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';

@Component({
    selector: 'jhi-attributes-pane',
    templateUrl: './attributes-pane.component.html'
})
export class AttributesPaneComponent implements OnInit {

    passportAttributes: GermplasmAttribute[] = [];
    attributes: GermplasmAttribute[] = [];

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private germplasmService: GermplasmService) {
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmAttributesByGidAndType(this.germplasmDetailsContext.gid, 'PASSPORT').toPromise().then((germplasmAttributes) => {
            this.passportAttributes = germplasmAttributes;
        });
        this.germplasmService.getGermplasmAttributesByGidAndType(this.germplasmDetailsContext.gid, 'ATTRIBUTE').toPromise().then((germplasmAttributes) => {
            this.attributes = germplasmAttributes;
        });
    }

}
