import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { METADATA_MANAGER_ROUTES } from './metadata-manager.route';
import { MetadataManagerComponent } from './metadata-manager.component';
import { NameTypeComponent } from './name-type/name-type.component';
import { NameTypeResolvePagingParams } from './name-type/name-type-resolve-paging-params';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(METADATA_MANAGER_ROUTES),
        RouterModule,
    ],
    declarations: [
        MetadataManagerComponent,
        NameTypeComponent
    ],
    entryComponents: [
        MetadataManagerComponent,
        NameTypeComponent
    ],
    providers: [
        NameTypeResolvePagingParams
    ]
})
export class MetadataManagerModule {

}
