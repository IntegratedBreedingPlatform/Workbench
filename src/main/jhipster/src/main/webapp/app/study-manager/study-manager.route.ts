import { Routes } from '@angular/router';
import { RouteAccessService } from '../shared';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';
import { AdvanceStudyComponent } from './advance/advance-study.component';
import { GermplasmListCreationPopupComponent } from '../germplasm-manager/germplasm-list/germplasm-list-creation-popup.component';
import { StudyManagerComponent } from './study-manager.component';
import { StudySearchComponent } from './study-search.component';
import { StudySummaryComponent } from './study-summary.component';
import { StudySummaryDatasetComponent } from './study-summary-dataset.component';
import { AdvanceSamplesComponent } from './advance/advance-samples.component';

export const STUDY_MANAGER_ROUTES: Routes = [
    {
        path: 'study-manager',
        component: StudyManagerComponent,
        children: [
            {
                path: '',
                redirectTo: 'study-search',
                pathMatch: 'full'
            },
            {
                path: 'study-search',
                component: StudySearchComponent,
                data: { authorities: ['ADMIN', 'MANAGE_STUDIES', 'STUDIES', 'VIEW_STUDIES'] },
                canActivate: [RouteAccessService]
            },
            {
                path: 'study/:studyId',
                component: StudySummaryComponent,
                data: { authorities: ['ADMIN', 'MANAGE_STUDIES', 'STUDIES', 'VIEW_STUDIES'] },
                canActivate: [RouteAccessService]
            },
            {
                path: 'study/:studyId/summary/dataset/:datasetId',
                component: StudySummaryDatasetComponent,
                data: { authorities: ['ADMIN', 'MANAGE_STUDIES', 'STUDIES', 'VIEW_STUDIES'] },
                canActivate: [RouteAccessService]
            }
        ]
    },
    {
        path: 'import-entry-details',
        component: ImportEntryDetailsComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'import-entry-details-dialog',
        component: ImportEntryDetailsPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'advance-study',
        component: AdvanceStudyComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'advance-samples',
        component: AdvanceSamplesComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'germplasm-list-creation-dialog',
        component: GermplasmListCreationPopupComponent,
        outlet: 'popup',
    }
];
