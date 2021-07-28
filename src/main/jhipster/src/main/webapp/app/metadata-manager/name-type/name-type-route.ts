import { Routes } from '@angular/router';
import { NameTypeEditPopupComponent } from './name-type-edit-dialog.component';
import { NameTypeComponent } from './name-type.component';
import { NameTypeResolvePagingParams } from './name-type-resolve-paging-params';
import { RouteAccessService } from '../../shared';
import { MANAGE_METADATA_PERMISSIONS } from '../../shared/auth/permissions';

export const nameTypeRoutes: Routes = [
    {
        path: '',
        redirectTo: 'name-type',
        pathMatch: 'full'
    }, {
        path: 'name-type',
        component: NameTypeComponent,
        resolve: {
            'pagingParams': NameTypeResolvePagingParams
        }
    }
];

export const nameTypePopupRoutes: Routes = [
    {
        path: 'name-type-edit-dialog',
        component: NameTypeEditPopupComponent,
        outlet: 'popup'
    }
];
