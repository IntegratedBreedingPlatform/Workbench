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
import { TreeTableComponent } from './tree-table/tree-table.component';
import { SampleTreeService } from './tree-table/sample-tree.service';

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
        TreeTableComponent
    ],
    entryComponents: [
        SampleComponent,
        SampleDialogComponent,
        SamplePopupComponent,
        SampleDeleteDialogComponent,
        SampleDeletePopupComponent,
        SampleBrowseComponent,
        TreeTableComponent
    ],
    providers: [
        SampleService,
        SamplePopupService,
        SampleResolvePagingParams,
        SampleTreeService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterSampleModule {}
