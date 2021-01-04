import { Routes } from '@angular/router';
import { LocationPopupComponent } from './location.component';

export const breedingLocationRoutes: Routes = [
    {
        path: 'location/:locationId',
        component: LocationPopupComponent,
        outlet: 'popup'
    }
];
