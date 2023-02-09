import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { ObservationDetailsComponent, ObservationDetailsPopupComponent } from './observation-details.component';
import { RouterModule } from '@angular/router';
import { observationDetailsRoutes } from './observation-details.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...observationDetailsRoutes]),
    ],
    declarations: [
        ObservationDetailsComponent,
        ObservationDetailsPopupComponent
    ],
    entryComponents: [
        ObservationDetailsComponent,
        ObservationDetailsPopupComponent
    ]
})
export class ObservationDetailsModule {

}
