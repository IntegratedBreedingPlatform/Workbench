import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BmsjHipsterSharedModule } from '../../shared';
import { SampleComponent, SampleManagerComponent, SampleResolvePagingParams, sampleRoute, SampleService } from './';
import { TreeTableComponent } from './tree-table/tree-table.component';
import { SampleTreeService } from './tree-table/sample-tree.service';

import { SampleSearchListComponent } from './sample-search-list.component';
import { SampleListService } from './sample-list.service';
import { SampleGenotypeService } from "./genotype/sample-genotype.service";
import { FileDownloadHelper } from './file-download.helper';
import { SampleImportPlateComponent } from './sample-import-plate.component';
import { ExcelService } from './excel.service';
import { SampleImportPlateMappingComponent } from './sample-import-plate-mapping.component';
import { SampleContext } from './sample.context';
import {GenotypeModalComponent} from "./genotype/genotype.modal.component";

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
        TreeTableComponent,
        SampleSearchListComponent,
        SampleImportPlateComponent,
        SampleImportPlateMappingComponent,
        GenotypeModalComponent
    ],
    entryComponents: [
        SampleComponent,
        SampleManagerComponent,
        TreeTableComponent,
        SampleSearchListComponent,
        SampleImportPlateComponent,
        SampleImportPlateMappingComponent,
        GenotypeModalComponent
    ],
    providers: [
        SampleService,
        SampleListService,
        SampleResolvePagingParams,
        SampleTreeService,
        FileDownloadHelper,
        ExcelService,
        SampleContext,
        SampleGenotypeService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterSampleModule {}
