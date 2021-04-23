import { Routes } from '@angular/router';
import { GermplasmAttributePopupComponent } from './germplasm-attribute-modal.component';

export const germplasmAttributeRoutes: Routes = [
    {
        path: 'germplasm-attribute-dialog/:gid',
        component: GermplasmAttributePopupComponent,
        outlet: 'popup'
    }
];
