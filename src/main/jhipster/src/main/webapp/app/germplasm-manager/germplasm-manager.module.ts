import { NgModule } from '@angular/core';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
import { RouterModule } from '@angular/router';
import { GERMPLASM_MANAGER_ROUTES } from './germplasm-manager.route';
import { BmsjHipsterSharedModule } from '../shared';
import { GermplasmSearchComponent } from './germplasm-search.component';
import { GermplasmSearchResolvePagingParams } from './germplasm-search-resolve-paging-params';
import { GermplasmManagerComponent } from './germplasm-manager.component';
import { GermplasmSelectorComponent } from './selector/germplasm-selector.component';
import { GermplasmListCreationComponent, GermplasmListCreationPopupComponent } from './germplasm-list/germplasm-list-creation.component';
import { GermplasmManagerContext } from './germplasm-manager.context';
import { GermplasmImportModule } from './import/germplasm-import.module';
import { GermplasmImportUpdateDialogComponent, GermplasmImportUpdatePopupComponent } from './germplasm-import-update-dialog.component';
import { GermplasmListAddComponent, GermplasmListAddPopupComponent } from './germplasm-list/germplasm-list-add.component';
import { GermplasmListService } from './germplasm-list/germplasm-list.service';
import { KeySequenceRegisterDeletionDialogComponent } from './key-sequence-register/key-sequence-register-deletion-dialog.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(GERMPLASM_MANAGER_ROUTES),
        GermplasmImportModule
    ],
    declarations: [
        LotCreationDialogComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationComponent,
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent,
        KeySequenceRegisterDeletionDialogComponent
    ],
    entryComponents: [
        LotCreationDialogComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationComponent,
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent,
        KeySequenceRegisterDeletionDialogComponent
    ],
    providers: [
        GermplasmSearchResolvePagingParams,
        GermplasmManagerContext,
        GermplasmListService
    ]
})
export class GermplasmManagerModule {

}
