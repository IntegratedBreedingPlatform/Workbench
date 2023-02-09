import { Routes } from '@angular/router';
import { ObservationDetailsComponent, ObservationDetailsPopupComponent } from './observation-details.component';
import { RouteAccessService } from '../../shared';

export const observationDetailsRoutes: Routes = [
    {
        path: 'observation-details/:observationUnitId',
        component: ObservationDetailsComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'observation-details-dialog/:observationUnitId',
        component: ObservationDetailsPopupComponent,
        outlet: 'popup'
    }
];
