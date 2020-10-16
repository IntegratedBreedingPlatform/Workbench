import { Routes } from '@angular/router';
import { BreedingMethodPopupComponent } from './breeding-method.component';

export const breedingMethodRoutes: Routes = [
    {
        path: 'breeding-method/:breedingMethodId',
        component: BreedingMethodPopupComponent,
        outlet: 'popup'
    }
];
