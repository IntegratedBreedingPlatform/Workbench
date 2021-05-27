import { Component, ViewEncapsulation } from '@angular/core';
import { GermplasmPedigreeService } from '../../shared/germplasm/service/germplasm.pedigree.service';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import { GermplasmNeighborhoodTreeComponent } from './germplasm-neighborhood-tree.component';

@Component({
    selector: 'jhi-germplasm-derivative-neighborhood-tree',
    templateUrl: './germplasm-neighborhood-tree.component.html',
    styleUrls: ['./germplasm-neighborhood-tree.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class GermplasmDerivativeNeighborhoodTreeComponent extends GermplasmNeighborhoodTreeComponent {

    constructor(public germplasmPedigreeService: GermplasmPedigreeService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService) {
        super();
    }

    getGermplasmNeighborhoodTreeNode() {
        return this.germplasmPedigreeService.getDerivativeNeighborhood(this.gid, this.numberOfStepsBackward, this.numberOfStepsForward);
    }

}
