import { Routes } from '@angular/router';
import { BreedingMethodPopupComponent } from './breeding-method.component';
import { BreedingMethodManagerPopupComponent } from './breeding-method-manager.component';

export const breedingMethodRoutes: Routes = [
    {
        path: 'breeding-method/:breedingMethodId',
        component: BreedingMethodPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'breeding-method-browser',
        component: BreedingMethodManagerPopupComponent,
        outlet: 'popup'
    }
];
