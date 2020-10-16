import { Routes } from '@angular/router';
import { GermplasmPopupComponent } from './germplasm.component';

export const germplasmRoutes: Routes = [
    {
        path: 'germplasm/:gid',
        component: GermplasmPopupComponent,
        outlet: 'popup'
    }
];
