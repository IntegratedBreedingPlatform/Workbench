import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { germplasmDetailsRoutes } from './germplasm-details.route';
import { GermplasmDetailsComponent } from './germplasm-details.component';
import { BasicDetailsPaneComponent } from './basic-details/basic-details-pane.component';
import { EditBasicDetailsPaneComponent, EditBasicDetailsPopupComponent } from './basic-details/edit-basic-details-pane.component';
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

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmDetailsRoutes]),
    ],
    declarations: [
        GermplasmDetailsPopupComponent,
        GermplasmDetailsModalComponent,
        GermplasmDetailsComponent,
        BasicDetailsPaneComponent,
        EditBasicDetailsPaneComponent,
        EditBasicDetailsPopupComponent,
        AttributesPaneComponent,
        PedigreePaneComponent,
        ObservationsPaneComponent,
        InventoryPaneComponent,
        ListsPaneComponent,
        SamplesPaneComponent
    ],
    entryComponents: [
        GermplasmDetailsPopupComponent,
        GermplasmDetailsModalComponent,
        GermplasmDetailsComponent,
        BasicDetailsPaneComponent,
        EditBasicDetailsPaneComponent,
        EditBasicDetailsPopupComponent,
        AttributesPaneComponent,
        PedigreePaneComponent,
        ObservationsPaneComponent,
        InventoryPaneComponent,
        ListsPaneComponent,
        SamplesPaneComponent
    ],
    providers: [
        GermplasmDetailsContext,
        GermplasmDetailsUrlService
    ]
})
export class GermplasmDetailsModule {

}
