import { Routes } from '@angular/router';
import { LotAttributeModalComponent, LotAttributePopupComponent } from './attribute/lot-attribute-modal.component';
import { RouteAccessService } from '../../shared';
import { LotAttributesPaneComponent } from './lot-attributes-pane.component';

export const lotAttributeRoutes: Routes = [
    {
        path: 'lot-attribute',
        component: LotAttributesPaneComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'lot-attribute-dialog',
        component: LotAttributeModalComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'lot-attribute-dialog/:lotId',
        component: LotAttributePopupComponent,
        outlet: 'popup'
    }
];
