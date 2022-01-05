import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { CropSettingsManagerComponent } from './crop-settings-manager.component';
import { NameTypesPaneComponent } from './name-types/name-types-pane.component';
import { NameTypeService } from '../shared/name-type/service/name-type.service';
import { NameTypeEditDialogComponent, NameTypeEditPopupComponent } from './name-types/name-type-edit-dialog.component';
import { NameTypeContext } from './name-types/name-type.context';
import { CROP_SETTINGS_MANAGER_ROUTES } from './crop-settings-manager.route';
import { LocationsPaneComponent } from './locations/locations-pane.component';
import { NameTypesResolvePagingParams } from './name-types/name-types-resolve-paging-params';

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
        NameTypeEditDialogComponent,
        NameTypeEditPopupComponent
    ],
    entryComponents: [
        CropSettingsManagerComponent,
        NameTypesPaneComponent,
        LocationsPaneComponent,
        NameTypeEditDialogComponent,
        NameTypeEditPopupComponent
    ],
    providers: [
        NameTypesResolvePagingParams,
        NameTypeService,
        NameTypeContext
    ]
})
export class CropSettingsManagerModule {

}
