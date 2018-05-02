import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    BmsjHipsterSharedLibsModule,
    BmsjHipsterSharedCommonModule,
    CSRFService,
    AuthServerProvider,
    AccountService,
    StateStorageService,
    LoginService,
    LoginModalService,
    JhiLoginModalComponent,
    Principal,
    HasAnyAuthorityDirective,
} from './';
import { DragDropModule, TreeTableModule } from 'primeng/primeng';

@NgModule({
    imports: [
        BmsjHipsterSharedLibsModule,
        BmsjHipsterSharedCommonModule
    ],
    declarations: [
        JhiLoginModalComponent,
        HasAnyAuthorityDirective
    ],
    providers: [
        LoginService,
        LoginModalService,
        AccountService,
        StateStorageService,
        Principal,
        CSRFService,
        AuthServerProvider,
        DatePipe
    ],
    entryComponents: [JhiLoginModalComponent],
    exports: [
        BmsjHipsterSharedCommonModule,
        JhiLoginModalComponent,
        HasAnyAuthorityDirective,
        DatePipe,
        TreeTableModule,
        DragDropModule,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {}
