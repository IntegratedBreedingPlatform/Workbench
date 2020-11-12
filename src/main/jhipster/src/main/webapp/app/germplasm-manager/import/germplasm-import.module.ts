import { NgModule } from '@angular/core';
import { GermplasmImportComponent, GermplasmImportPopupComponent } from './germplasm-import.component';
import { BmsjHipsterSharedModule } from '../../shared';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
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
