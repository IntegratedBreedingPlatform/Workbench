import { Routes } from '@angular/router';
import { RouteAccessService } from '../../shared';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';

export const STUDY_MANAGER_ROUTES: Routes = [
    {
        path: 'import-entry-details',
        component: ImportEntryDetailsComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'import-entry-details-dialog',
        component: ImportEntryDetailsPopupComponent,
        outlet: 'popup'
    }
];
