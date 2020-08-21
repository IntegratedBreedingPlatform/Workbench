import { Routes } from '@angular/router';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
import { RouteAccessService } from '../shared';

export const GERMPLASM_MANAGER_ROUTES: Routes = [
    {
        path: 'lot-creation-dialog',
        component: LotCreationDialogComponent,
        data: {
            authorities: [
                'ADMIN',
                'CROP_MANAGEMENT',
                'BREEDING_ACTIVITIES',
                'MANAGE_GERMPLASM',
                'MG_MANAGE_INVENTORY',
                'MG_CREATE_LOTS',
                'MANAGE_STUDIES',
                'MS_CREATE_LOTS'
            ]
        },
        canActivate: [RouteAccessService]
    }
]
