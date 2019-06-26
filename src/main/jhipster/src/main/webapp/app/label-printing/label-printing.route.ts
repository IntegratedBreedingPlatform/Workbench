import { Routes } from '@angular/router';

import { LabelPrintingComponent } from './label-printing.component';
import { RouteAccessService } from '../shared';

export const LABEL_PRINTING_ROUTES: Routes = [
  {
    path: 'label-printing',
    component: LabelPrintingComponent,
    data: {
      authorities: ['SUPERADMIN', 'ADMIN', 'BREEDING_ACTIVITIES', 'MANAGE_STUDIES']
    },
    canActivate: [RouteAccessService]
  }
];
