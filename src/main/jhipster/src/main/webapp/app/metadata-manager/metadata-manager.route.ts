import { Routes } from '@angular/router';
import { MetadataManagerComponent } from './metadata-manager.component';
import { RouteAccessService } from '../shared';
import { MANAGE_CROP_METADATA_PERMISSIONS } from '../shared/auth/permissions';
import { nameTypePopupRoutes, nameTypeRoutes } from './name-type/name-type-route';

export const METADATA_MANAGER_ROUTES: Routes = [
    ...nameTypePopupRoutes,
    {
        path: 'metadata-manager',
        component: MetadataManagerComponent,
        data: {
            pageTitle: 'metadata-manager.title',
            authorities: [...MANAGE_CROP_METADATA_PERMISSIONS]
        },
        canActivate: [RouteAccessService],
        children: [...nameTypeRoutes,
        ],
    }

];
