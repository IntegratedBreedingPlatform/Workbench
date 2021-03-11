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
import { GermplasmDetailsDialogComponent, GermplasmDetailsPopupComponent } from './germplasm-details-dialog.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmDetailsRoutes]),
    ],
    declarations: [
        GermplasmDetailsPopupComponent,
        GermplasmDetailsDialogComponent,
        GermplasmDetailsComponent,
        BasicDetailsPaneComponent,
        AttributesPaneComponent,
        PedigreePaneComponent,
        ObservationsPaneComponent,
        InventoryPaneComponent,
        ListsPaneComponent,
        SamplesPaneComponent
    ],
    entryComponents: [
        GermplasmDetailsPopupComponent,
        GermplasmDetailsDialogComponent,
        GermplasmDetailsComponent,
        BasicDetailsPaneComponent,
        AttributesPaneComponent,
        PedigreePaneComponent,
        ObservationsPaneComponent,
        InventoryPaneComponent,
        ListsPaneComponent,
        SamplesPaneComponent
    ],
    providers: [
        GermplasmDetailsContext
    ]
})
export class GermplasmDetailsModule {

}
