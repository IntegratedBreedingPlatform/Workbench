import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { GermplasmListComponent } from './germplasm-list.component';
import { GERMPLASM_LIST_ROUTES } from './germplasm-list.route';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { ListComponent } from './list.component';
import { GermplasmListImportModule } from './import/germplasm-list-import.module';
import { ListColumnsComponent } from './list-columns.component';
import { ListDataRowComponent } from './list-table-row-data.component';
import { GermplasmListReorderEntriesDialogComponent } from './reorder-entries/germplasm-list-reorder-entries-dialog.component';
import { GermplasmListMetadataComponent } from './germplasm-list-metadata.component';
import { GermplasmListClonePopupComponent } from './germplasm-list-clone-popup.component';
import { GermplasmListManagerContext } from './germplasm-list-manager.context';

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
        GermplasmListReorderEntriesDialogComponent,
        GermplasmListMetadataComponent,
        GermplasmListClonePopupComponent
    ],
    entryComponents: [
        GermplasmListComponent,
        GermplasmListSearchComponent,
        ListComponent,
        ListColumnsComponent,
        ListDataRowComponent,
        GermplasmListReorderEntriesDialogComponent,
        GermplasmListMetadataComponent,
        GermplasmListClonePopupComponent
    ],
    providers: [
        GermplasmListService,
        GermplasmListManagerContext
    ]
})
export class GermplasmListModule {

}
