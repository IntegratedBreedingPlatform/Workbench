import { Routes } from '@angular/router';
import { RouteAccessService } from '../../shared';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';
import { StudyEntryVariableMatchesComponent } from './entry-details/study-entry-variable-matches.component';

export const STUDY_MANAGER_ROUTES: Routes = [
    {
        path: 'import-entry-details',
        component: ImportEntryDetailsComponent,
        canActivate: [RouteAccessService],
        children: [
            {
                path: '',
                redirectTo: 'study-entry-variable-matches',
                pathMatch: 'full'
            },
            {
                path: 'study-entry-variable-matches',
                component: StudyEntryVariableMatchesComponent,
                canActivate: [RouteAccessService]
            }
        ]
    },
    {
        path: 'import-entry-details-dialog',
        component: ImportEntryDetailsPopupComponent,
        outlet: 'popup'
    }
];
