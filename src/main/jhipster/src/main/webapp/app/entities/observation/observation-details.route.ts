import { Routes } from '@angular/router';
import { ObservationDetailsComponent } from './observation-details.component';
import { RouteAccessService } from '../../shared';

export const observationDetailsRoutes: Routes = [
    {
        path: 'observation-details/:observationUnitId',
        component: ObservationDetailsComponent,
        canActivate: [RouteAccessService]
    }
];
