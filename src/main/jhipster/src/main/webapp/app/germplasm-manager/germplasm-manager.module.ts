import { NgModule } from '@angular/core';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
import { RouterModule } from '@angular/router';
import { GERMPLASM_MANAGER_ROUTES } from './germplasm-manager.route';
import { BmsjHipsterSharedModule } from '../shared';
import { GermplasmTabComponent } from './germplasm-tab.component';
import { GermplasmSearchComponent } from './germplasm-search.component';
import { GermplasmSearchResolvePagingParams } from './germplasm-search-resolve-paging-params';
import { GermplasmTreeTableComponent } from '../shared/tree/germplasm/germplasm-tree-table.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(GERMPLASM_MANAGER_ROUTES)
    ],
    declarations: [
        LotCreationDialogComponent,
        GermplasmTabComponent,
        GermplasmSearchComponent,
        GermplasmTreeTableComponent
    ],
    entryComponents: [
        LotCreationDialogComponent,
        GermplasmTabComponent,
        GermplasmSearchComponent,
        GermplasmTreeTableComponent
    ],
    providers: [
        GermplasmSearchResolvePagingParams
    ]
})
export class GermplasmManagerModule {

}
