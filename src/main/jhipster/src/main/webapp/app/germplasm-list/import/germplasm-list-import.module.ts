import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { GermplasmListImportComponent, GermplasmListImportPopupComponent } from './germplasm-list-import.component';
import { GermplasmListService } from '../../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListImportReviewComponent } from './germplasm-list-import-review.component';
import { GermplasmListImportMatchesComponent } from './germplasm-list-import-matches.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule
    ],
    declarations: [
        GermplasmListImportComponent,
        GermplasmListImportPopupComponent,
        GermplasmListImportReviewComponent,
        GermplasmListImportMatchesComponent
    ],
    entryComponents: [
        GermplasmListImportComponent,
        GermplasmListImportPopupComponent,
        GermplasmListImportReviewComponent,
        GermplasmListImportMatchesComponent
    ],
    providers: [GermplasmListService
    ]
})
export class GermplasmListImportModule {

}
