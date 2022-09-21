import { Routes } from '@angular/router';
import { BreedingMethodComponent, BreedingMethodPopupComponent } from './breeding-method.component';
import { BreedingMethodManagerPopupComponent } from './breeding-method-manager.component';
import { RouteAccessService } from '../../shared';

export const breedingMethodRoutes: Routes = [
    {
        path: 'breeding-method-page/:breedingMethodCode',
        component: BreedingMethodComponent,
        canActivate: [RouteAccessService]
    },
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
