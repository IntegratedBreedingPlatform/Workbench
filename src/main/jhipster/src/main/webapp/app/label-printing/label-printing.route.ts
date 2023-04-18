import { Routes } from '@angular/router';

import { LabelPrintingComponent } from './label-printing.component';
import { RouteAccessService } from '../shared';
import {
  GERMPLASM_LABEL_PRINTING_PERMISSIONS,
  GERMPLASM_LIST_LABEL_PRINTING_PERMISSIONS,
  LOT_LABEL_PRINTING_PERMISSIONS,
  OBSERVATION_DATASET_LABEL_PRINTING_PERMISSIONS,
  STUDY_ENTRIES_LABEL_PRINTING_PERMISSIONS
} from '../shared/auth/permissions';

export const LABEL_PRINTING_ROUTES: Routes = [
    {
        path: 'label-printing',
        component: LabelPrintingComponent,
        data: {
            authorities: [
                ...OBSERVATION_DATASET_LABEL_PRINTING_PERMISSIONS,
                ...STUDY_ENTRIES_LABEL_PRINTING_PERMISSIONS,
                ...LOT_LABEL_PRINTING_PERMISSIONS,
                ...GERMPLASM_LABEL_PRINTING_PERMISSIONS,
                ...GERMPLASM_LIST_LABEL_PRINTING_PERMISSIONS
            ]
        },
        canActivate: [RouteAccessService]
    }
];
