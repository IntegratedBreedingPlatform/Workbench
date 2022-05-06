import { Routes } from '@angular/router';
import { CropSettingsManagerComponent } from './crop-settings-manager.component';
import { RouteAccessService } from '../shared';
import { MANAGE_CROP_SETTINGS_PERMISSIONS } from '../shared/auth/permissions';
import { LocationsPaneComponent } from './locations/locations-pane.component';
import { NameTypesPaneComponent } from './name-types/name-types-pane.component';
import { NameTypeEditPopupComponent } from './name-types/name-type-edit-dialog.component';
import { LocationEditPopupComponent } from './locations/location-edit-dialog.component';
import { BreedingMethodsPaneComponent } from './breeding-methods/breeding-methods-pane.component';
import { BreedingMethodEditPopupComponent } from './breeding-methods/breeding-method-edit-dialog.component';
import { ParametersPaneComponent } from './parameters/parameters-pane.component';

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
                path: 'breeding-methods',
                component: BreedingMethodsPaneComponent
            },
            {
                path: 'name-types',
                component: NameTypesPaneComponent
            },
            {
                path: 'parameters',
                component: ParametersPaneComponent
            }
        ],
    },
    {
        path: 'name-type-edit-dialog',
        component: NameTypeEditPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'location-edit-dialog',
        component: LocationEditPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'breeding-method-edit-dialog',
        component: BreedingMethodEditPopupComponent,
        outlet: 'popup'
    }

];
