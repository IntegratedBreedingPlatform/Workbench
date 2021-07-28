import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { METADATA_MANAGER_ROUTES } from './metadata-manager.route';
import { MetadataManagerComponent } from './metadata-manager.component';
import { NameTypeComponent } from './name-type/name-type.component';
import { NameTypeResolvePagingParams } from './name-type/name-type-resolve-paging-params';
import { NameTypeService } from './name-type/name-type.service';
import { NameTypeEditDialogComponent, NameTypeEditPopupComponent } from './name-type/name-type-edit-dialog.component';
import { NameTypeContext } from './name-type/name-type.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(METADATA_MANAGER_ROUTES),
        RouterModule,
    ],
    declarations: [
        MetadataManagerComponent,
        NameTypeComponent,
        NameTypeEditDialogComponent,
        NameTypeEditPopupComponent
    ],
    entryComponents: [
        MetadataManagerComponent,
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
export class MetadataManagerModule {

}
