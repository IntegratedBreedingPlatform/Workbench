import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { ObservationDetailsComponent } from './observation-details.component';
import { RouterModule } from '@angular/router';
import { observationDetailsRoutes } from './observation-details.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...observationDetailsRoutes]),
    ],
    declarations: [
        ObservationDetailsComponent
    ],
    entryComponents: [
        ObservationDetailsComponent
    ]
})
export class ObservationDetailsModule {

}
