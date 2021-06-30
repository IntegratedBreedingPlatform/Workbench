import { NgModule } from '@angular/core';
import { GermplasmImportComponent, GermplasmImportPopupComponent } from './germplasm-import.component';
import { BmsjHipsterSharedModule } from '../../shared';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';
import { RouterModule } from '@angular/router';
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';
import { GermplasmImportReviewComponent, NameColumnPipePipe } from './germplasm-import-review.component';
import { GermplasmImportContext } from './germplasm-import.context';
import { GermplasmImportMatchesComponent } from './germplasm-import-matches.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule
    ],
    declarations: [
        GermplasmImportComponent,
        GermplasmImportPopupComponent,
        GermplasmImportBasicDetailsComponent,
        GermplasmImportInventoryComponent,
        GermplasmImportMatchesComponent,
        GermplasmImportReviewComponent,
        NameColumnPipePipe
    ],
    entryComponents: [
        GermplasmImportComponent,
        GermplasmImportPopupComponent,
        GermplasmImportBasicDetailsComponent,
        GermplasmImportInventoryComponent,
        GermplasmImportMatchesComponent,
        GermplasmImportReviewComponent
    ],
    providers: [
        GermplasmImportContext
    ]
})
export class GermplasmImportModule {

}
