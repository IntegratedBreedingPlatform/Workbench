import { Component, Input } from '@angular/core';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';

@Component({
    selector: 'jhi-germplasm-table',
    templateUrl: './germplasm-table.component.html'
})
export class GermplasmTableComponent {

    @Input() germplasmList: GermplasmDto[] = [];

    constructor(public germplasmDetailsUrlService: GermplasmDetailsUrlService) {
    }
}
