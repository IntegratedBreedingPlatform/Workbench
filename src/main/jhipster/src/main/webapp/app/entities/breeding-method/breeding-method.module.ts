import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { BreedingMethodComponent, BreedingMethodPopupComponent } from './breeding-method.component';
import { RouterModule } from '@angular/router';
import { breedingMethodRoutes } from './breeding-method.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...breedingMethodRoutes]),
    ],
    declarations: [
        BreedingMethodComponent,
        BreedingMethodPopupComponent
    ],
    entryComponents: [
        BreedingMethodComponent,
        BreedingMethodPopupComponent
    ]
})
export class BreedingMethodModule {

}
