import { Routes } from '@angular/router';
import { GermplasmAttributeAuditPopupComponent } from './germplasm-attribute-audit-modal.component';

export const germplasmAttributeAuditRoutes: Routes = [
    {
        path: 'germplasm/:gid/attribute/:attributeId/audit-dialog',
        component: GermplasmAttributeAuditPopupComponent,
        outlet: 'popup'
    }
];
