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
        SampleBrowseComponent
    ],
    entryComponents: [
        SampleComponent,
        SampleDialogComponent,
        SamplePopupComponent,
        SampleDeleteDialogComponent,
        SampleDeletePopupComponent,
        SampleBrowseComponent
    ],
    providers: [
        SampleService,
        SamplePopupService,
        SampleResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterSampleModule {}
