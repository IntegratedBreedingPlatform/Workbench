import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import { AccountService, BmsjHipsterSharedCommonModule, BmsjHipsterSharedLibsModule, Principal } from './';
import { ModalComponent } from './modal/modal.component';
import { ModalConfirmComponent } from './modal/modal-confirm.component';
import { ModalService } from './modal/modal.service';
import { DragDropModule } from 'primeng/primeng';
import { TreeTableModule } from 'primeng/treetable';
import { SharedModule } from 'primeng/shared';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { HasNotAnyAuthorityDirective } from './auth/has-not-any-authority.directive';
import { InventoryService } from './inventory/service/inventory.service';
import { LotService } from './inventory/service/lot.service';
import { TransactionService } from './inventory/service/transaction.service';
import { ParamContext } from './service/param.context';
import { CustomMinGreaterThanValidatorDirective } from './validators/custom-min-greater-than-validator.directive';
import { HelpService } from './service/help.service';
import { ColumnFilterComponent } from './column-filter/column-filter.component';
import { ColVisButtonComponent } from './col-vis/col-vis-button.component';
import { ColumnFilterNumberComponent } from './column-filter/column-filter-number-component';
import { ColumnFilterDateComponent } from './column-filter/column-filter-date-component';
import { ColumnFilterChecklistComponent } from './column-filter/column-filter-checklist-component';
import { ColumnFilterTextComponent } from './column-filter/column-filter-text-component';
import { ColumnFilterRadioComponent } from './column-filter/column-filter-radio-component';
import { ColumnFilterListComponent } from './column-filter/column-filter-list-component';
import { ReactiveFormsModule } from '@angular/forms';
import { GermplasmService } from './germplasm/service/germplasm.service';
import { StudyTreeComponent } from './tree/study/study-tree.component';
import { ColumnFilterBooleanComponent } from './column-filter/column-filter-boolean-component';
import { ColumnFilterTextWithMatchOptionsComponent } from './column-filter/column-filter-text-with-match-options-component';
import { ColumnFilterPedigreeOptionsComponent } from './column-filter/column-filter-pedigree-options-component';

@NgModule({
    imports: [
        BmsjHipsterSharedLibsModule,
        BmsjHipsterSharedCommonModule,
        ReactiveFormsModule
    ],
    declarations: [
        ModalComponent,
        ModalConfirmComponent,
        HasAnyAuthorityDirective,
        HasNotAnyAuthorityDirective,
        CustomMinGreaterThanValidatorDirective,
        ColumnFilterNumberComponent,
        ColumnFilterDateComponent,
        ColumnFilterChecklistComponent,
        ColumnFilterTextComponent,
        ColumnFilterRadioComponent,
        ColumnFilterListComponent,
        ColumnFilterComponent,
        ColVisButtonComponent,
        ColumnFilterBooleanComponent,
        ColumnFilterTextWithMatchOptionsComponent,
        ColumnFilterPedigreeOptionsComponent,
        StudyTreeComponent
    ],
    providers: [
        DatePipe,
        ModalService,
        Principal,
        AccountService,
        InventoryService,
        LotService,
        TransactionService,
        ParamContext,
        HelpService,
        GermplasmService
    ],
    entryComponents: [
        ModalComponent,
        ModalConfirmComponent,
        StudyTreeComponent
    ],
    exports: [
        BmsjHipsterSharedCommonModule,
        SharedModule,
        TreeTableModule,
        DragDropModule,
        DatePipe,
        ModalComponent,
        ModalConfirmComponent,
        HasAnyAuthorityDirective,
        HasNotAnyAuthorityDirective,
        CustomMinGreaterThanValidatorDirective,
        ColumnFilterNumberComponent,
        ColumnFilterDateComponent,
        ColumnFilterChecklistComponent,
        ColumnFilterTextComponent,
        ColumnFilterRadioComponent,
        ColumnFilterListComponent,
        ColumnFilterComponent,
        ColVisButtonComponent,
        ColumnFilterBooleanComponent,
        ColumnFilterTextWithMatchOptionsComponent,
        ColumnFilterPedigreeOptionsComponent,
        StudyTreeComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {
}
