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
    SampleBrowseComponent,
    SampleBrowsePopupComponent,
    SampleBrowseDialogComponent
} from './';
import { SampleTreeService } from './sample-browse/sample-tree.service';

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
        SampleBrowseDialogComponent,
        SampleBrowsePopupComponent
    ],
    entryComponents: [
        SampleComponent,
        SampleDialogComponent,
        SamplePopupComponent,
        SampleDeleteDialogComponent,
        SampleDeletePopupComponent,
        SampleBrowseComponent,
        SampleBrowseDialogComponent,
        SampleBrowsePopupComponent
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
