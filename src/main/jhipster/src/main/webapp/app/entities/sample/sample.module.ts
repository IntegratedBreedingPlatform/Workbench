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

import {SampleSearchListComponent} from './sample-search-list.component';
import {SampleListService} from './sample-list.service';

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
        SampleListService,
        SamplePopupService,
        SampleResolvePagingParams,
        SampleTreeService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterSampleModule {}
