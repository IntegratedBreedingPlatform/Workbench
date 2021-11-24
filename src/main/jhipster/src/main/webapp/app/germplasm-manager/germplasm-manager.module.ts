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
import { GermplasmListClonePopupComponent } from './germplasm-list/germplasm-list-clone-popup.component';
import { GermplasmManagerContext } from './germplasm-manager.context';
import { GermplasmImportModule } from './import/germplasm-import.module';
import { GermplasmImportUpdateDialogComponent, GermplasmImportUpdatePopupComponent } from './germplasm-import-update-dialog.component';
import { GermplasmListAddComponent, GermplasmListAddPopupComponent } from './germplasm-list/germplasm-list-add.component';
import { KeySequenceRegisterDeletionDialogComponent } from './key-sequence-register/key-sequence-register-deletion-dialog.component';
import { GermplasmGroupOptionsDialogComponent } from './grouping/germplasm-group-options-dialog-component';
import { GermplasmDetailsUrlService } from '../shared/germplasm/service/germplasm-details.url.service';
import { GermplasmSelectorModalComponent, GermplasmSelectorPopupComponent } from './selector/germplasm-selector-modal.component';
import { GermplasmGroupingResultComponent } from './grouping/germplasm-grouping-result.component';
import { GermplasmCodingDialogComponent } from './coding/germplasm-coding-dialog.component';
import { GermplasmCodingResultDialogComponent } from './coding/germplasm-coding-result-dialog.component';
import { GermplasmImportUpdateDescriptorsConfirmationDialogComponent } from './germplasm-import-update-descriptors-confirmation-dialog.component';
import { MergeGermplasmSelectionComponent } from './merge/merge-germplasm-selection-component';
import { MergeGermplasmExistingLotsComponent } from './merge/merge-germplasm-existing-lots.component';
import { GermplasmProgenyModalComponent, GermplasmProgenyPopupComponent } from './merge/germplasm-progeny-modal.component';
import { MergeGermplasmReviewComponent } from './merge/merge-germplasm-review.component';

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
        GermplasmListClonePopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdateDescriptorsConfirmationDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmSelectorModalComponent,
        GermplasmSelectorPopupComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent,
        KeySequenceRegisterDeletionDialogComponent,
        GermplasmGroupOptionsDialogComponent,
        GermplasmGroupingResultComponent,
        GermplasmCodingDialogComponent,
        GermplasmCodingResultDialogComponent,
        MergeGermplasmSelectionComponent,
        MergeGermplasmExistingLotsComponent,
        MergeGermplasmReviewComponent,
        GermplasmProgenyModalComponent,
        GermplasmProgenyPopupComponent
    ],
    entryComponents: [
        LotCreationDialogComponent,
        LotCreationPopupComponent,
        GermplasmManagerComponent,
        GermplasmSearchComponent,
        GermplasmListCreationPopupComponent,
        GermplasmListClonePopupComponent,
        GermplasmSelectorComponent,
        GermplasmImportUpdateDialogComponent,
        GermplasmImportUpdateDescriptorsConfirmationDialogComponent,
        GermplasmImportUpdatePopupComponent,
        GermplasmSelectorComponent,
        GermplasmSelectorModalComponent,
        GermplasmSelectorPopupComponent,
        GermplasmListAddComponent,
        GermplasmListAddPopupComponent,
        KeySequenceRegisterDeletionDialogComponent,
        GermplasmGroupOptionsDialogComponent,
        GermplasmGroupingResultComponent,
        GermplasmCodingDialogComponent,
        GermplasmCodingResultDialogComponent,
        MergeGermplasmSelectionComponent,
        MergeGermplasmExistingLotsComponent,
        MergeGermplasmReviewComponent,
        GermplasmProgenyModalComponent,
        GermplasmProgenyPopupComponent
    ],
    providers: [
        GermplasmSearchResolvePagingParams,
        GermplasmManagerContext,
        GermplasmDetailsUrlService
    ]
})
export class GermplasmManagerModule {

}
