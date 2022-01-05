import { Routes } from '@angular/router';
import { CropSettingsManagerComponent } from './crop-settings-manager.component';
import { RouteAccessService } from '../shared';
import { MANAGE_CROP_SETTINGS_PERMISSIONS } from '../shared/auth/permissions';
import { LocationsPaneComponent } from './locations/locations-pane.component';
import { NameTypesPaneComponent } from './name-types/name-types-pane.component';
import { NameTypesResolvePagingParams } from './name-types/name-types-resolve-paging-params';
import { NameTypeEditPopupComponent } from './name-types/name-type-edit-dialog.component';

export const CROP_SETTINGS_MANAGER_ROUTES: Routes = [
    {
        path: 'crop-settings-manager',
        component: CropSettingsManagerComponent,
        data: {
            pageTitle: 'crop-settings-manager.title',
            authorities: [...MANAGE_CROP_SETTINGS_PERMISSIONS]
        },
        canActivate: [RouteAccessService],
        children: [
            {
                path: '',
                redirectTo: 'locations',
                pathMatch: 'full'
            },
            {
                path: 'locations',
                component: LocationsPaneComponent
            },
            {
                path: 'name-types',
                component: NameTypesPaneComponent,
                resolve: {
                    'pagingParams': NameTypesResolvePagingParams
                }
            }
        ],
    },
    {
        path: 'name-type-edit-dialog',
        component: NameTypeEditPopupComponent,
        outlet: 'popup'
    }

];
