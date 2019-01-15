import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    BmsjHipsterSharedLibsModule,
    BmsjHipsterSharedCommonModule,
    AuthServerProvider
} from './';

@NgModule({
    imports: [
        BmsjHipsterSharedLibsModule,
        BmsjHipsterSharedCommonModule
    ],
    declarations: [
    ],
    providers: [
        AuthServerProvider,
        DatePipe
    ],
    entryComponents: [],
    exports: [
        BmsjHipsterSharedCommonModule,
        DatePipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {}
