import { Routes } from '@angular/router';
import { GermplasmProgenitorsPopupComponent } from './germplasm-progenitors-modal.component';

export const germplasmProgenitorsRoutes: Routes = [
    {
        path: 'germplasm-progenitors-dialog/:gid',
        component: GermplasmProgenitorsPopupComponent,
        outlet: 'popup'
    }
];
