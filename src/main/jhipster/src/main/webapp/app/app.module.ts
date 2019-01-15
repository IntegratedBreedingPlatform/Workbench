import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Ng2Webstorage, LocalStorageService, SessionStorageService  } from 'ngx-webstorage';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { BmsjHipsterSharedModule } from './shared';
import { BmsjHipsterAppRoutingModule} from './app-routing.module';
import { BmsjHipsterHomeModule } from './home/home.module';
import { BmsjHipsterEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import {
    JhiMainComponent,
    PageRibbonComponent
} from './layouts';
import { PrintLabelsModule } from './print-labels/print-labels.module';

@NgModule({
    imports: [
        BrowserModule,
        BmsjHipsterAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        BmsjHipsterSharedModule,
        BmsjHipsterHomeModule,
        BmsjHipsterEntityModule,
        PrintLabelsModule
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        PageRibbonComponent
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
            deps: [
                LocalStorageService,
                SessionStorageService
            ]
        }
    ],
    bootstrap: [ JhiMainComponent ]
})
export class BmsjHipsterAppModule {}
