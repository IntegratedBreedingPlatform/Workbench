import { Routes } from '@angular/router';
import { GermplasmNameAuditPopupComponent } from './germplasm-name-audit.component';

export const germplasmNameAuditRoutes: Routes = [
    {
        path: 'germplasm/:gid/name/:nameId/audit-dialog',
        component: GermplasmNameAuditPopupComponent,
        outlet: 'popup'
    }
];
