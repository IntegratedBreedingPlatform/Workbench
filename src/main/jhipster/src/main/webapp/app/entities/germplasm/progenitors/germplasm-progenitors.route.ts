import { Routes } from '@angular/router';
import { GermplasmProgenitorsPopupComponent } from './germplasm-progenitors-modal.component';

export const germplasmProgenitorsRoutes: Routes = [
    {
        path: 'germplasm-progenitors-dialog',
        component: GermplasmProgenitorsPopupComponent,
        outlet: 'popup'
    }
];
