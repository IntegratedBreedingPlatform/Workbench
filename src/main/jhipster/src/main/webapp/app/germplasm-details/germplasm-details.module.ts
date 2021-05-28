import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { germplasmDetailsRoutes } from './germplasm-details.route';
import { GermplasmDetailsComponent } from './germplasm-details.component';
import { BasicDetailsPaneComponent } from './basic-details/basic-details-pane.component';
import { AttributesPaneComponent } from './attributes/attributes-pane.component';
import { PedigreePaneComponent } from './pedigree/pedigree-pane.component';
import { ObservationsPaneComponent } from './observations/observations-pane.component';
import { InventoryPaneComponent } from './inventory/inventory-pane.component';
import { ListsPaneComponent } from './lists/lists-pane.component';
import { SamplesPaneComponent } from './samples/samples-pane.component';
import { BmsjHipsterSharedModule } from '../shared';
import { GermplasmDetailsContext } from './germplasm-details.context';
import { GermplasmDetailsModalComponent, GermplasmDetailsPopupComponent } from './germplasm-details-modal.component';
import { GermplasmDetailsUrlService } from '../shared/germplasm/service/germplasm-details.url.service';
import { PedigreeTreeComponent } from './pedigree/pedigree-tree.component';
import { GenerationHistoryComponent } from './pedigree/generation-history.component';
import { GermplasmTableComponent } from './pedigree/germplasm-table.component';
import { ManagementNeighborsComponent } from './pedigree/management-neighbors.component';
import { GroupRelativesComponent } from './pedigree/group-relatives.component';
import { GermplasmNeighborhoodTreeComponent } from './pedigree/germplasm-neighborhood-tree.component';
import { GermplasmDetailsGraphvizModalComponent, GermplasmDetailsGraphvizModalPopupComponent } from './germplasm-details-graphviz-modal.component';
import { GermplasmDerivativeNeighborhoodTreeComponent } from './pedigree/germplasm-derivative-neighborhood-tree.component';
import { GermplasmMaintenanceNeighborhoodTreeComponent } from './pedigree/germplasm-maintenance-neighborhood-tree.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmDetailsRoutes]),
    ],
    declarations: [
        GermplasmDetailsPopupComponent,
        GermplasmDetailsModalComponent,
        GermplasmDetailsGraphvizModalPopupComponent,
        GermplasmDetailsGraphvizModalComponent,
        GermplasmDetailsComponent,
        BasicDetailsPaneComponent,
        AttributesPaneComponent,
        PedigreePaneComponent,
        ObservationsPaneComponent,
        InventoryPaneComponent,
        ListsPaneComponent,
        SamplesPaneComponent,
        PedigreeTreeComponent,
        GermplasmTableComponent,
        GenerationHistoryComponent,
        ManagementNeighborsComponent,
        GroupRelativesComponent,
        GermplasmDerivativeNeighborhoodTreeComponent,
        GermplasmMaintenanceNeighborhoodTreeComponent
    ],
    entryComponents: [
        GermplasmDetailsPopupComponent,
        GermplasmDetailsModalComponent,
        GermplasmDetailsGraphvizModalPopupComponent,
        GermplasmDetailsGraphvizModalComponent,
        GermplasmDetailsComponent,
        BasicDetailsPaneComponent,
        AttributesPaneComponent,
        PedigreePaneComponent,
        ObservationsPaneComponent,
        InventoryPaneComponent,
        ListsPaneComponent,
        SamplesPaneComponent,
        PedigreeTreeComponent,
        GermplasmTableComponent,
        GenerationHistoryComponent,
        ManagementNeighborsComponent,
        GroupRelativesComponent,
        GermplasmDerivativeNeighborhoodTreeComponent,
        GermplasmMaintenanceNeighborhoodTreeComponent
    ],
    providers: [
        GermplasmDetailsContext,
        GermplasmDetailsUrlService
    ]
})
export class GermplasmDetailsModule {

}
