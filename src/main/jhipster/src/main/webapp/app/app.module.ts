import './vendor.ts';

import { Injector, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { LocalStorageService, Ng2Webstorage, SessionStorageService } from 'ngx-webstorage';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { BmsjHipsterSharedModule, RouteAccessService } from './shared';
import { BmsjHipsterAppRoutingModule } from './app-routing.module';
import { BmsjHipsterEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { ErrorComponent, JhiMainComponent, PageRibbonComponent } from './layouts';
import { LabelPrintingModule } from './label-printing/label-printing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GermplasmManagerModule } from './germplasm-manager/germplasm-manager.module';
import { GermplasmTreeTableComponent } from './shared/tree/germplasm/germplasm-tree-table.component';
import { StudyTreeComponent } from './shared/tree/study/study-tree.component';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { InventoryDetailsModule } from './germplasm-manager/inventory/details/inventory-details.module';
import { NavbarModule } from './navbar/navbar.module';
import { GermplasmDetailsModule } from './germplasm-details/germplasm-details.module';
import { ReleaseNotesModule } from './release-notes/release-notes.module';
import { UserProfileModule } from './entities/user-profile/user-profile.module';
import { FileManagerModule } from './file-manager/file-manager.module';
import { PrototypeModule } from './prototype/prototype.module';
import { VariableDetailsModule } from './ontology/variable-details/variable-details.module';
import { MetadataManagerModule } from './metadata-manager/metadata-manager.module';
import { GermplasmListModule } from './germplasm-list/germplasm-list.module';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        BmsjHipsterAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-' }),
        BmsjHipsterSharedModule,
        BmsjHipsterEntityModule,
        NavbarModule,
        LabelPrintingModule,
        GermplasmManagerModule,
        InventoryDetailsModule,
        GermplasmDetailsModule,
        ReleaseNotesModule,
        UserProfileModule,
        FileManagerModule,
        PrototypeModule,
        MetadataManagerModule,
        VariableDetailsModule,
        GermplasmListModule
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        PageRibbonComponent,
        ErrorComponent,
        GermplasmTreeTableComponent,
        StudyTreeComponent
    ],
    entryComponents: [
        GermplasmTreeTableComponent,
        StudyTreeComponent
    ],
    providers: [
        RouteAccessService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
            deps: [
                LocalStorageService,
                SessionStorageService
            ]
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true,
            deps: [
                Injector
            ]
        }
    ],
    bootstrap: [JhiMainComponent]
})
export class BmsjHipsterAppModule {
}
