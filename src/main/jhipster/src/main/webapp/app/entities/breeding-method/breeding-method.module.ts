import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { BreedingMethodComponent, BreedingMethodPopupComponent } from './breeding-method.component';
import { RouterModule } from '@angular/router';
import { breedingMethodRoutes } from './breeding-method.route';
import { BreedingMethodManagerComponent, BreedingMethodManagerPopupComponent } from './breeding-method-manager.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...breedingMethodRoutes]),
    ],
    declarations: [
        BreedingMethodComponent,
        BreedingMethodPopupComponent,
        BreedingMethodManagerComponent,
        BreedingMethodManagerPopupComponent
    ],
    entryComponents: [
        BreedingMethodComponent,
        BreedingMethodPopupComponent,
        BreedingMethodManagerComponent,
        BreedingMethodManagerPopupComponent
    ]
})
export class BreedingMethodModule {

}
