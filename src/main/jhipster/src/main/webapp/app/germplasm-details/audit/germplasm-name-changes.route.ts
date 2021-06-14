import { Routes } from '@angular/router';
import { GermplasmNameChangesPopupComponent } from './germplasm-name-changes-modal.component';

export const germplasmNameChangesRoutes: Routes = [
    {
        path: 'germplasm/:gid/name/:nameId/changes-dialog',
        component: GermplasmNameChangesPopupComponent,
        outlet: 'popup'
    }
];
