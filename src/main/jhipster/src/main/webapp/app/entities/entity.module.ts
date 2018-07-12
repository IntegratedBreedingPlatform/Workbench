import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { BmsjHipsterSampleModule } from './sample/sample.module';
import {FileDownloadHelper} from './sample/file-download.helper';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        BmsjHipsterSampleModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [FileDownloadHelper],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterEntityModule {}
