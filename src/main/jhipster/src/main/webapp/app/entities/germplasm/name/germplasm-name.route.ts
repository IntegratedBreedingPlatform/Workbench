import { Routes } from '@angular/router';
import { GermplasmNamePopupComponent } from './germplasm-name-modal.component';

export const germplasmNameRoutes: Routes = [
    {
        path: 'germplasm-name-dialog/:gid',
        component: GermplasmNamePopupComponent,
        outlet: 'popup'
    }
];
