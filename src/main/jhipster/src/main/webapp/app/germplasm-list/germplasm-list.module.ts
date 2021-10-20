import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { GermplasmListComponent } from './germplasm-list.component';
import { GERMPLASM_LIST_ROUTES } from './germplasm-list.route';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { ListComponent } from './list.component';
import { GermplasmListImportModule } from './import/germplasm-list-import.module';
import { GermplasmListImportContext } from './import/germplasm-list-import.context';
import { ListColumnsComponent } from './list-columns.component';
import { ListDataRowComponent } from './list-table-row-data.component';
import { GermplasmListContext } from './germplasm-list.context';
import { GermplasmListReorderEntriesComponent, GermplasmListReorderEntriesPopupComponent } from './reorder-entries/germplasm-list-reorder-entries.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(GERMPLASM_LIST_ROUTES),
        GermplasmListImportModule
    ],
    declarations: [
        GermplasmListComponent,
        GermplasmListSearchComponent,
        ListComponent,
        ListColumnsComponent,
        ListDataRowComponent,
        GermplasmListReorderEntriesComponent,
        GermplasmListReorderEntriesPopupComponent
    ],
    entryComponents: [
        GermplasmListComponent,
        GermplasmListSearchComponent,
        ListComponent,
        ListColumnsComponent,
        ListDataRowComponent,
        GermplasmListReorderEntriesComponent,
        GermplasmListReorderEntriesPopupComponent
    ],
    providers: [
        GermplasmListService,
        GermplasmListImportContext,
        GermplasmListContext
    ]
})
export class GermplasmListModule {

}
