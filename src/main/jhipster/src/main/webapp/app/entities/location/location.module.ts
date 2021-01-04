import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { LocationComponent, LocationPopupComponent } from './location.component';
import { breedingLocationRoutes } from './location.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...breedingLocationRoutes]),
    ],
    declarations: [
        LocationComponent,
        LocationPopupComponent
    ],
    entryComponents: [
        LocationComponent,
        LocationPopupComponent
    ]
})
export class LocationModule {

}
