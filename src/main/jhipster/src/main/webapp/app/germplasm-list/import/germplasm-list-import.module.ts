import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { GERMPLASM_MANAGER_ROUTES } from '../../germplasm-manager/germplasm-manager.route';
import { GermplasmImportModule } from '../../germplasm-manager/import/germplasm-import.module';
import { GermplasmListImportComponent, ListImportPopupComponent } from './germplasm-list-import.component';
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
        ListImportPopupComponent,
        GermplasmListImportReviewComponent,
        GermplasmListImportMatchesComponent
    ],
    entryComponents: [
        GermplasmListImportComponent,
        ListImportPopupComponent,
        GermplasmListImportReviewComponent,
        GermplasmListImportMatchesComponent
    ],
    providers: [GermplasmListService
    ]
})
export class GermplasmListImportModule {

}
