import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { CropSettingsManagerComponent } from './crop-settings-manager.component';
import { NameTypeComponent } from './name-type/name-type.component';
import { NameTypeResolvePagingParams } from './name-type/name-type-resolve-paging-params';
import { NameTypeService } from '../shared/name-type/service/name-type.service';
import { NameTypeEditDialogComponent, NameTypeEditPopupComponent } from './name-type/name-type-edit-dialog.component';
import { NameTypeContext } from './name-type/name-type.context';
import { CROP_SETTINGS_MANAGER_ROUTES } from './crop-settings-manager.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(CROP_SETTINGS_MANAGER_ROUTES),
        RouterModule,
    ],
    declarations: [
        CropSettingsManagerComponent,
        NameTypeComponent,
        NameTypeEditDialogComponent,
        NameTypeEditPopupComponent
    ],
    entryComponents: [
        CropSettingsManagerComponent,
        NameTypeComponent,
        NameTypeEditDialogComponent,
        NameTypeEditPopupComponent
    ],
    providers: [
        NameTypeResolvePagingParams,
        NameTypeService,
        NameTypeContext
    ]
})
export class CropSettingsManagerModule {

}
