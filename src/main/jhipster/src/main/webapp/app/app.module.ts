import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ngx-webstorage';

import { BmsjHipsterSharedModule, UserRouteAccessService } from './shared';
import { BmsjHipsterAppRoutingModule} from './app-routing.module';
import { BmsjHipsterHomeModule } from './home/home.module';
import { BmsjHipsterAdminModule } from './admin/admin.module';
import { BmsjHipsterAccountModule } from './account/account.module';
import { BmsjHipsterEntityModule } from './entities/entity.module';
import { customHttpProvider } from './blocks/interceptor/http.provider';
import { PaginationConfig } from './blocks/config/uib-pagination.config';

// jhipster-needle-angular-add-module-import JHipster will add new module here

import {
    JhiMainComponent,
    NavbarComponent,
    FooterComponent,
    ProfileService,
    PageRibbonComponent,
    ActiveMenuDirective,
    ErrorComponent
} from './layouts';

@NgModule({
    imports: [
        BrowserModule,
        BmsjHipsterAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        BmsjHipsterSharedModule,
        BmsjHipsterHomeModule,
        BmsjHipsterAdminModule,
        BmsjHipsterAccountModule,
        BmsjHipsterEntityModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        ActiveMenuDirective,
        FooterComponent
    ],
    providers: [
        ProfileService,
        customHttpProvider(),
        PaginationConfig,
        UserRouteAccessService
    ],
    bootstrap: [ JhiMainComponent ]
})
export class BmsjHipsterAppModule {}
