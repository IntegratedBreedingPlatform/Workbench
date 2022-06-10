import { Routes } from '@angular/router';
import { LotAttributeModalComponent, LotAttributePopupComponent } from './lot-attribute-modal.component';
import { VariableDetailsComponent } from '../../../ontology/variable-details/variable-details.component';
import { RouteAccessService } from '../../../shared';
import { DetailsComponent } from '../../../ontology/variable-details/details.component';
import { ValidValuesComponent } from '../../../ontology/variable-details/valid-values.component';

export const lotAttributeRoutes: Routes = [
    {
        path: 'lot-attribute',
        component: LotAttributeModalComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'lot-attribute-dialog/:lotId',
        component: LotAttributePopupComponent,
        outlet: 'popup'
    }
];
