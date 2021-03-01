import { NgModule } from '@angular/core';
import { LotCreationDialogComponent, LotCreationPopupComponent } from './inventory/lot-creation-dialog.component';
import { RouterModule } from '@angular/router';
import { GERMPLASM_MANAGER_ROUTES } from './germplasm-manager.route';
import { BmsjHipsterSharedModule } from '../shared';
import { GermplasmSearchComponent } from './germplasm-search.component';
import { GermplasmSearchResolvePagingParams } from './germplasm-search-resolve-paging-params';
import { GermplasmManagerComponent } from './germplasm-manager.component';
import { GermplasmSelectorComponent } from './selector/germplasm-selector.component';
import { GermplasmListCreationPopupComponent } from './germplasm-list/germplasm-list-creation-popup.component';
import { GermplasmManagerContext } from './germplasm-manager.context';
import { GermplasmImportModule } from './import/germplasm-import.module';
import { GermplasmImportUpdateDialogComponent, GermplasmImportUpdatePopupComponent } from './germplasm-import-update-dialog.component';
import { GermplasmListAddComponent, GermplasmListAddPopupComponent } from './germplasm-list/germplasm-list-add.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(GERMPLASM_MANAGER_ROUTES),
        GermplasmImportModule
    ],
    declarations: [
        LotCreationDialogComponent,
        LotCreationPopupComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent
    ],
    entryComponents: [
        LotCreationDialogComponent,
        LotCreationPopupComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent
    ],
    providers: [
        GermplasmSearchResolvePagingParams,
        GermplasmManagerContext,
    ]
})
export class GermplasmManagerModule {

}
