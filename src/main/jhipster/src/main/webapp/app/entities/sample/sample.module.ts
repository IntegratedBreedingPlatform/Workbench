import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BmsjHipsterSharedModule } from '../../shared';
import {
    SampleService,
    SampleComponent,
    sampleRoute,
    SampleResolvePagingParams,
    SampleManagerComponent
} from './';

import {SampleSearchListComponent} from './sample-search-list.component';
import {SampleListService} from './sample-list.service';
import {FileDownloadHelper} from './file-download.helper';
import {SampleImportPlateComponent} from './sample-import-plate.component';
import {ModalComponent} from '../../shared/modal/modal.component';
import {ModalService} from '../../shared/modal/modal.service';
import {ExcelService} from './excel.service';
import {SampleImportPlateMappingComponent} from './sample-import-plate-mapping.component';
import {SampleContext} from './sample.context';

const ENTITY_STATES = [
    ...sampleRoute,
];

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        SampleComponent,
        SampleManagerComponent,
        SampleSearchListComponent,
        SampleImportPlateComponent,
        SampleImportPlateMappingComponent,
        ModalComponent
    ],
    entryComponents: [
        SampleComponent,
        SampleManagerComponent,
        SampleImportPlateComponent,
        SampleImportPlateMappingComponent,
        ModalComponent
    ],
    providers: [
        SampleService,
        SampleListService,
        SampleResolvePagingParams,
        FileDownloadHelper,
        ModalService,
        ExcelService,
        SampleContext
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterSampleModule {}
