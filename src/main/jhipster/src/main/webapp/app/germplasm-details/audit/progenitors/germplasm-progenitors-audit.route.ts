import { Routes } from '@angular/router';
import { DetailsAuditComponent } from './details-audit.component';
import { RouteAccessService } from '../../../shared';
import { GermplasmProgenitorsAuditComponent } from './germplasm-progenitors-audit.component';
import { GermplasmBasicDetailsAuditPopupComponent } from './germplasm-progenitors-audit-modal.component';
import { OtherProgenitorsAuditComponent } from './other-progenitors-audit.component';

export const germplasmProgenitorDetailsAuditRoutes: Routes = [
    {
        path: 'germplasm/:gid/progenitors/audit-dialog',
        component: GermplasmBasicDetailsAuditPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'germplasm/progenitors/audit',
        component: GermplasmProgenitorsAuditComponent,
        canActivate: [RouteAccessService],
        children: [
            {
                path: '',
                redirectTo: 'details',
                pathMatch: 'full'
            },
            {
                path: 'details',
                component: DetailsAuditComponent
            },
            {
                path: 'other-progenitors',
                component: OtherProgenitorsAuditComponent
            }
        ]
    }
];
