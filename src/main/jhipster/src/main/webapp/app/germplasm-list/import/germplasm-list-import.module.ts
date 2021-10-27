import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { GermplasmListImportComponent, GermplasmListImportPopupComponent } from './germplasm-list-import.component';
import { GermplasmListService } from '../../shared/germplasm-list/service/germplasm-list.service';
import { GermplasmListImportReviewComponent } from './germplasm-list-import-review.component';
import { GermplasmListImportMultiMatchesComponent } from './germplasm-list-import-multi-matches.component';
import { GermplasmListImportManualMatchesComponent } from './germplasm-list-import-manual-matches.component';
import { GermplasmListImportUpdateComponent, GermplasmListImportUpdatePopupComponent } from './germplasm-list-import-update.component';
import { GermplasmListVariableMatchesComponent } from './germplasm-list-variable-matches.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule
    ],
    declarations: [
        GermplasmListImportComponent,
        GermplasmListImportPopupComponent,
        GermplasmListImportReviewComponent,
        GermplasmListImportMultiMatchesComponent,
        GermplasmListImportManualMatchesComponent,
        GermplasmListImportUpdateComponent,
        GermplasmListImportUpdatePopupComponent,
        GermplasmListVariableMatchesComponent
    ],
    entryComponents: [
        GermplasmListImportComponent,
        GermplasmListImportPopupComponent,
        GermplasmListImportReviewComponent,
        GermplasmListImportMultiMatchesComponent,
        GermplasmListImportManualMatchesComponent,
        GermplasmListImportUpdateComponent,
        GermplasmListImportUpdatePopupComponent,
        GermplasmListVariableMatchesComponent
    ],
    providers: [GermplasmListService
    ]
})
export class GermplasmListImportModule {

}
