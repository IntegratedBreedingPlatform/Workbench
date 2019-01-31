import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    BmsjHipsterSharedLibsModule,
    BmsjHipsterSharedCommonModule,
    AuthServerProvider
} from './';
import { DragDropModule } from 'primeng/primeng';
import { TreeTableModule } from 'primeng/treetable';
import { SharedModule } from 'primeng/shared';

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
        SharedModule,
        TreeTableModule,
        DragDropModule,
        DatePipe
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {}
