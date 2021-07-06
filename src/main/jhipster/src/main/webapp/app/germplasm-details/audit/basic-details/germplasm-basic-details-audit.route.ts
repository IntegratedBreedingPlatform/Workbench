import { Routes } from '@angular/router';
import { BasicDetailsAuditComponent } from './basic-details-audit.component';
import { RouteAccessService } from '../../../shared';
import { GermplasmBasicDetailsAuditComponent } from './germplasm-basic-details-audit.component';
import { GermplasmBasicDetailsAuditPopupComponent } from './germplasm-basic-details-audit-modal.component';
import { ReferenceAuditComponent } from './reference-audit.component';

export const germplasmBasicDetailsAuditRoutes: Routes = [
    {
        path: 'germplasm/:gid/basic-details/audit-dialog',
        component: GermplasmBasicDetailsAuditPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'germplasm/audit',
        component: GermplasmBasicDetailsAuditComponent,
        canActivate: [RouteAccessService],
        children: [
            {
                path: '',
                redirectTo: 'basic-details',
                pathMatch: 'full'
            },
            {
                path: 'basic-details',
                component: BasicDetailsAuditComponent
            },
            {
                path: 'reference',
                component: ReferenceAuditComponent
            }
        ]
    }
];
