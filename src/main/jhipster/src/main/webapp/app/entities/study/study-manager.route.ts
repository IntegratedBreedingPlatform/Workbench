import { Routes } from '@angular/router';
import { RouteAccessService } from '../../shared';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';
import { AdvanceStudyComponent } from './advance/advance-study.component';
import { GermplasmListCreationPopupComponent } from '../../germplasm-manager/germplasm-list/germplasm-list-creation-popup.component';

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
    },
    {
        path: 'advance-study',
        component: AdvanceStudyComponent,
        canActivate: [RouteAccessService]
    },
    {
        path: 'germplasm-list-creation-dialog',
        component: GermplasmListCreationPopupComponent,
        outlet: 'popup',
    }
];
