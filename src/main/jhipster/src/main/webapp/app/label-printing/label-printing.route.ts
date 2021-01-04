import { Routes } from '@angular/router';

import { LabelPrintingComponent } from './label-printing.component';
import { RouteAccessService } from '../shared';
import { LOT_LABEL_PRINTING_PERMISSIONS } from '../shared/auth/permissions';

export const LABEL_PRINTING_ROUTES: Routes = [
  {
    path: 'label-printing',
    component: LabelPrintingComponent,
    data: {
      authorities: [
        'ADMIN',
        'STUDIES',
        'MANAGE_STUDIES',
        ...LOT_LABEL_PRINTING_PERMISSIONS
      ]
    },
    canActivate: [RouteAccessService]
  }
];
