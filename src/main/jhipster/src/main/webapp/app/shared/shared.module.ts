import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    BmsjHipsterSharedLibsModule,
    BmsjHipsterSharedCommonModule, Principal, AccountService
} from './';
import { ModalComponent } from './modal/modal.component';
import { ModalConfirmComponent } from './modal/modal-confirm.component';
import { ModalService } from './modal/modal.service';

@NgModule({
    imports: [
        BmsjHipsterSharedLibsModule,
        BmsjHipsterSharedCommonModule
    ],
    declarations: [
        ModalComponent,
        ModalConfirmComponent
    ],
    providers: [
        DatePipe,
        ModalService,
        Principal,
        AccountService
    ],
    entryComponents: [
        ModalComponent,
        ModalConfirmComponent
    ],
    exports: [
        BmsjHipsterSharedCommonModule,
        DatePipe,
        ModalComponent,
        ModalConfirmComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {}
