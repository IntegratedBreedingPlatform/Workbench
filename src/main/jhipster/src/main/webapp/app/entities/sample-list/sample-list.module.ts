import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BmsjHipsterSharedModule } from '../../shared';
import {
    SampleListService,
    SampleListPopupService,
    SampleListComponent,
    SampleListDetailComponent,
    SampleListDialogComponent,
    SampleListPopupComponent,
    SampleListDeletePopupComponent,
    SampleListDeleteDialogComponent,
    sampleListRoute,
    sampleListPopupRoute,
    SampleListResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...sampleListRoute,
    ...sampleListPopupRoute,
];

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        SampleListComponent,
        SampleListDetailComponent,
        SampleListDialogComponent,
        SampleListDeleteDialogComponent,
        SampleListPopupComponent,
        SampleListDeletePopupComponent,
    ],
    entryComponents: [
        SampleListComponent,
        SampleListDialogComponent,
        SampleListPopupComponent,
        SampleListDeleteDialogComponent,
        SampleListDeletePopupComponent,
    ],
    providers: [
        SampleListService,
        SampleListPopupService,
        SampleListResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterSampleListModule {}
