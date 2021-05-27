import { Component, ViewEncapsulation } from '@angular/core';
import { GermplasmPedigreeService } from '../../shared/germplasm/service/germplasm.pedigree.service';
import { GermplasmNeighborhoodTreeComponent } from './germplasm-neighborhood-tree.component';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';

@Component({
    selector: 'jhi-germplasm-maintenance-neighborhood-tree',
    templateUrl: './germplasm-neighborhood-tree.component.html',
    styleUrls: ['./germplasm-neighborhood-tree.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class GermplasmMaintenanceNeighborhoodTreeComponent extends GermplasmNeighborhoodTreeComponent {

    constructor(public germplasmPedigreeService: GermplasmPedigreeService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService) {
        super();
    }

    getGermplasmNeighborhoodTreeNode() {
        return this.germplasmPedigreeService.getMaintenanceNeighborhood(this.gid, this.numberOfStepsBackward, this.numberOfStepsForward);
    }

}
