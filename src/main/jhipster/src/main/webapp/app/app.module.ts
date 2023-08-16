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
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { InventoryDetailsModule } from './germplasm-manager/inventory/details/inventory-details.module';
import { NavbarModule } from './navbar/navbar.module';
import { GermplasmDetailsModule } from './germplasm-details/germplasm-details.module';
import { ReleaseNotesModule } from './release-notes/release-notes.module';
import { UserProfileModule } from './entities/user-profile/user-profile.module';
import { FileManagerModule } from './file-manager/file-manager.module';
import { PrototypeModule } from './prototype/prototype.module';
import { VariableDetailsModule } from './ontology/variable-details/variable-details.module';
import { CropSettingsManagerModule } from './crop-settings-manager/crop-settings-manager.module';
import { GermplasmListModule } from './germplasm-list/germplasm-list.module';
import { CopModule } from './cop/cop.module';
import { AboutModule } from './about/about.module';
import { ProgramSettingsManagerModule } from './program-settings-manager/program-settings-manager.module';
import { CreateProgramModule } from './entities/create-program/create-program.module';
import { LotAttributeModule } from './entities/lot/lot-attribute.module';
import { StudyManagerModule } from './study-manager/study-manager.module';
import { SiteAdminModule } from './site-admin/site-admin.module';
import { ObservationDetailsModule } from './entities/observation/observation-details.module';
import {CrossPlanManagerModule} from "./cross-plan-manager/cross-plan-manager.module";

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
        ObservationDetailsModule,
        ReleaseNotesModule,
        UserProfileModule,
        FileManagerModule,
        CopModule,
        PrototypeModule,
        CropSettingsManagerModule,
        VariableDetailsModule,
        GermplasmListModule,
        CrossPlanManagerModule,
        ProgramSettingsManagerModule,
        AboutModule,
        CreateProgramModule,
        LotAttributeModule,
        StudyManagerModule,
        SiteAdminModule
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        PageRibbonComponent,
        ErrorComponent
    ],
    entryComponents: [
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
