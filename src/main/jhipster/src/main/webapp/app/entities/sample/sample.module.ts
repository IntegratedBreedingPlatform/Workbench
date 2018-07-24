import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BmsjHipsterSharedModule } from '../../shared';
import {
    SampleService,
    SamplePopupService,
    SampleComponent,
    SampleDetailComponent,
    SampleDialogComponent,
    SamplePopupComponent,
    SampleDeletePopupComponent,
    SampleDeleteDialogComponent,
    sampleRoute,
    samplePopupRoute,
    SampleResolvePagingParams,
    SampleBrowseComponent
} from './';

import {SampleSearchListComponent} from './sample-search-list.component';
import {SampleListService} from './sample-list.service';
import {FileDownloadHelper} from './file-download.helper';
import {SampleImportPlateComponent} from './sample-import-plate.component';
import {ModalComponent} from '../../shared/modal/modal.component';
import {ModalService} from '../../shared/modal/modal.service';
import {ExcelService} from './excel.service';
import {SampleImportPlateMappingComponent} from './sample-import-plate-mapping.component';

const ENTITY_STATES = [
    ...sampleRoute,
    ...samplePopupRoute,
];

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        SampleComponent,
        SampleDetailComponent,
        SampleDialogComponent,
        SampleDeleteDialogComponent,
        SamplePopupComponent,
        SampleDeletePopupComponent,
        SampleBrowseComponent,
        SampleSearchListComponent,
        SampleImportPlateComponent,
        SampleImportPlateMappingComponent,
        ModalComponent
    ],
    entryComponents: [
        SampleComponent,
        SampleDialogComponent,
        SamplePopupComponent,
        SampleDeleteDialogComponent,
        SampleDeletePopupComponent,
        SampleBrowseComponent,
        SampleImportPlateComponent,
        SampleImportPlateMappingComponent,
        ModalComponent
    ],
    providers: [
        SampleService,
        SampleListService,
        SamplePopupService,
        SampleResolvePagingParams,
        FileDownloadHelper,
        ModalService,
        ExcelService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterSampleModule {}
