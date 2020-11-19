import { NgModule } from '@angular/core';
import { GermplasmImportComponent, GermplasmImportPopupComponent } from './germplasm-import.component';
import { BmsjHipsterSharedModule } from '../../shared';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';
import { RouterModule } from '@angular/router';
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';
import { GermplasmImportReviewComponent } from './germplasm-import-review.component';
import { TableModule } from 'primeng/table';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule,
        TableModule
    ],
    declarations: [
        GermplasmImportComponent,
        GermplasmImportPopupComponent,
        GermplasmImportBasicDetailsComponent,
        GermplasmImportInventoryComponent,
        GermplasmImportReviewComponent
    ],
    entryComponents: [
        GermplasmImportComponent,
        GermplasmImportPopupComponent,
        GermplasmImportBasicDetailsComponent,
        GermplasmImportInventoryComponent,
        GermplasmImportReviewComponent
    ]
})
export class GermplasmImportModule {

}
