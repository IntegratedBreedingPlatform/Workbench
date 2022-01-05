import { Routes } from '@angular/router';
import { CropSettingsManagerComponent } from './crop-settings-manager.component';
import { RouteAccessService } from '../shared';
import { MANAGE_CROP_SETTINGS_PERMISSIONS } from '../shared/auth/permissions';
import { nameTypePopupRoutes, nameTypeRoutes } from './name-type/name-type-route';

export const CROP_SETTINGS_MANAGER_ROUTES: Routes = [
    ...nameTypePopupRoutes,
    {
        path: 'crop-settings-manager',
        component: CropSettingsManagerComponent,
        data: {
            pageTitle: 'crop-settings-manager.title',
            authorities: [...MANAGE_CROP_SETTINGS_PERMISSIONS]
        },
        canActivate: [RouteAccessService],
        children: [...nameTypeRoutes,
        ],
    }

];
