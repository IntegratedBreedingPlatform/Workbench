import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { ObservationDetailsComponent } from './observation-details.component';
import { RouterModule } from '@angular/router';
import { observationDetailsRoutes } from './observation-details.route';
import { ObservationService } from './observation.service';

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
    ],
    providers: [
        ObservationService
    ]
})
export class ObservationDetailsModule {

}
