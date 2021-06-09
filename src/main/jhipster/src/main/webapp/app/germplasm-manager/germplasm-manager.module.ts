import { NgModule } from '@angular/core';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
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
import { KeySequenceRegisterDeletionDialogComponent } from './key-sequence-register/key-sequence-register-deletion-dialog.component';
import { GermplasmGroupOptionsDialogComponent } from './grouping/germplasm-group-options-dialog-component';
import { GermplasmDetailsUrlService } from '../shared/germplasm/service/germplasm-details.url.service';
import { GermplasmSelectorModalComponent, GermplasmSelectorPopupComponent } from './selector/germplasm-selector-modal.component';
import { GermplasmGroupingResultComponent } from './grouping/germplasm-grouping-result.component';

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
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmSelectorModalComponent,
        GermplasmSelectorPopupComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent,
        KeySequenceRegisterDeletionDialogComponent,
        GermplasmGroupOptionsDialogComponent,
        GermplasmGroupingResultComponent
    ],
    entryComponents: [
        LotCreationDialogComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationPopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmSelectorModalComponent,
        GermplasmSelectorPopupComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent,
        KeySequenceRegisterDeletionDialogComponent,
        GermplasmGroupOptionsDialogComponent,
        GermplasmGroupingResultComponent
    ],
    providers: [
        GermplasmSearchResolvePagingParams,
        GermplasmManagerContext,
        GermplasmDetailsUrlService
    ]
})
export class GermplasmManagerModule {

}
