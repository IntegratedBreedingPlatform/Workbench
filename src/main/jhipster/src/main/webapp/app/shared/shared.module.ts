import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    BmsjHipsterSharedLibsModule,
    BmsjHipsterSharedCommonModule,
    AuthServerProvider
} from './';
import { ModalComponent } from './modal/modal.component';
import { modalConfirmComponent } from './modal/modal-confirm.component';
import { ModalService } from './modal/modal.service';

@NgModule({
    imports: [
        BmsjHipsterSharedLibsModule,
        BmsjHipsterSharedCommonModule
    ],
    declarations: [
        ModalComponent,
        modalConfirmComponent
    ],
    providers: [
        AuthServerProvider,
        DatePipe,
        ModalService
    ],
    entryComponents: [
        ModalComponent,
        modalConfirmComponent
    ],
    exports: [
        BmsjHipsterSharedCommonModule,
        DatePipe,
        ModalComponent,
        modalConfirmComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {}
