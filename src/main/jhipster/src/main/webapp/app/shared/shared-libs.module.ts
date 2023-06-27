import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { NgJhipsterModule } from 'ng-jhipster';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CookieModule } from 'ngx-cookie';
import { NgSelect2Module } from 'ng-select2';
import { TreeDragDropService } from 'primeng/api';
import { NgSelectModule } from '@ng-select/ng-select';
import { MatMenuModule } from '@angular/material/menu';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {TranslateModule, TranslateService} from '@ngx-translate/core';

@NgModule({
    imports: [
        NgbModule,
        NgJhipsterModule.forRoot({
            // set below to true to make alerts look like toast
            alertAsToast: true,
            i18nEnabled: true,
            defaultI18nLang: 'en'
        }),
        InfiniteScrollModule,
        CookieModule.forRoot(),
        TranslateModule
    ],
    exports: [
        FormsModule,
        HttpClientModule,
        CommonModule,
        NgbModule,
        NgJhipsterModule,
        InfiniteScrollModule,
        NgSelect2Module,
        NgSelectModule,
        MatMenuModule,
        TranslateModule
    ],
    providers: [
        TreeDragDropService,
        TranslateService
    ]
})
export class BmsjHipsterSharedLibsModule {}
