import { Routes } from '@angular/router';
import { MetadataManagerComponent } from './metadata-manager.component';
import { NameTypeComponent } from './name-type/name-type.component';
import { NameTypeResolvePagingParams } from './name-type/name-type-resolve-paging-params';
import { RouteAccessService } from '../shared';
import { MANAGE_METADATA_PERMISSIONS } from '../shared/auth/permissions';
import { nameTypePopupRoutes, nameTypeRoutes } from './name-type/name-type-route';
import { NameTypeEditPopupComponent } from './name-type/name-type-edit-dialog.component';

export const METADATA_MANAGER_ROUTES: Routes = [
    ...nameTypePopupRoutes,
    {
        path: 'metadata-manager',
        component: MetadataManagerComponent,
        data: {
            pageTitle: 'metadata-manager.title',
            authorities: [...MANAGE_METADATA_PERMISSIONS]
        },
        canActivate: [RouteAccessService],
        children: [...nameTypeRoutes,
        ],
    }

];
