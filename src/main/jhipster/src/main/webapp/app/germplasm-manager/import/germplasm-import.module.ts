import { NgModule } from '@angular/core';
import { GermplasmImportComponent, GermplasmImportPopupComponent } from './germplasm-import.component';
import { BmsjHipsterSharedModule } from '../../shared';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule
    ],
    declarations: [
        GermplasmImportComponent,
        GermplasmImportPopupComponent,
        GermplasmImportBasicDetailsComponent
    ],
    entryComponents: [
        GermplasmImportComponent,
        GermplasmImportPopupComponent,
        GermplasmImportBasicDetailsComponent
    ]
})
export class GermplasmImportModule {

}
