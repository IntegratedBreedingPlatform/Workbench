import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { CropSettingsManagerComponent } from './crop-settings-manager.component';
import { NameTypesPaneComponent } from './name-types/name-types-pane.component';
import { NameTypeService } from '../shared/name-type/service/name-type.service';
import { NameTypeEditDialogComponent, NameTypeEditPopupComponent } from './name-types/name-type-edit-dialog.component';
import { CROP_SETTINGS_MANAGER_ROUTES } from './crop-settings-manager.route';
import { LocationsPaneComponent } from './locations/locations-pane.component';
import { NameTypesResolvePagingParams } from './name-types/name-types-resolve-paging-params';
import { CropSettingsContext } from './crop-Settings.context';
import { LocationEditDialogComponent, LocationEditPopupComponent } from './locations/location-edit-dialog.component';
import { BreedingMethodsPaneComponent } from './breeding-methods/breeding-methods-pane.component';
import { BreedingMethodEditDialogComponent, BreedingMethodEditPopupComponent } from './breeding-methods/breeding-method-edit-dialog.component';
import { ParametersPaneComponent } from './parameters/parameters-pane.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(CROP_SETTINGS_MANAGER_ROUTES),
        RouterModule,
    ],
    declarations: [
        CropSettingsManagerComponent,
        NameTypesPaneComponent,
        LocationsPaneComponent,
        BreedingMethodsPaneComponent,
        NameTypeEditDialogComponent,
        NameTypeEditPopupComponent,
        LocationEditDialogComponent,
        LocationEditPopupComponent,
        BreedingMethodEditDialogComponent,
        BreedingMethodEditPopupComponent,
        ParametersPaneComponent
    ],
    entryComponents: [
        CropSettingsManagerComponent,
        NameTypesPaneComponent,
        LocationsPaneComponent,
        BreedingMethodsPaneComponent,
        NameTypeEditDialogComponent,
        NameTypeEditPopupComponent,
        LocationEditDialogComponent,
        LocationEditPopupComponent,
        BreedingMethodEditDialogComponent,
        BreedingMethodEditPopupComponent,
        ParametersPaneComponent
    ],
    providers: [
        NameTypesResolvePagingParams,
        NameTypeService,
        CropSettingsContext
    ]
})
export class CropSettingsManagerModule {

}
