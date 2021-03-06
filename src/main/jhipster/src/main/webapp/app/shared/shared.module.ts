import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import { AccountService, BmsjHipsterSharedCommonModule, BmsjHipsterSharedLibsModule, Principal } from './';
import { DragDropModule, TreeModule } from 'primeng/primeng';
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
import { ColumnFilterNumberRangeComponent } from './column-filter/column-filter-number-range-component';
import { ColumnFilterDateComponent } from './column-filter/column-filter-date-component';
import { ColumnFilterChecklistComponent } from './column-filter/column-filter-checklist-component';
import { ColumnFilterTextComponent } from './column-filter/column-filter-text-component';
import { ColumnFilterRadioComponent } from './column-filter/column-filter-radio-component';
import { ColumnFilterListComponent } from './column-filter/column-filter-list-component';
import { ReactiveFormsModule } from '@angular/forms';
import { GermplasmService } from './germplasm/service/germplasm.service';
import { ColumnFilterBooleanComponent } from './column-filter/column-filter-boolean-component';
import { ColumnFilterTextWithMatchOptionsComponent } from './column-filter/column-filter-text-with-match-options-component';
import { ColumnFilterPedigreeOptionsComponent } from './column-filter/column-filter-pedigree-options-component';
import { ColumnFilterAttributesComponent } from './column-filter/column-filter-attributes-component';
import { ColumnFilterNameTypesComponent } from './column-filter/column-filter-name-types-component';
import { AttributesService } from './attributes/service/attributes.service';
import { NameTypeService } from './name-type/service/name-type.service';
import { KeyValuePipe } from './util/keyvalue.pipe';
import { PopupService } from './modal/popup.service';
import { LocationService } from './location/service/location.service';
import { BreedingMethodService } from './breeding-method/service/breeding-method.service';
import { LoginService } from './login/login.service';
import { ModalComponent } from './modal/modal.component';
import { ModalConfirmComponent } from './modal/modal-confirm.component';
import { CustomMinEqualsValidatorDirective } from './validators/custom-min-equals-validator.directive';
import { ItemCountCustomComponent } from './component/item-count-custom.component';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from './alert/alert.service';
import { BreedingMethodSelect2DataPipe } from './breeding-method/model/breeding-method-select2.pipe';
import { LocationSelect2DataPipe } from './location/model/location-select2.pipe';
import { ListBuilderComponent } from './list-builder/list-builder.component';
import { ListBuilderContext } from './list-builder/list-builder.context';
import { TableModule } from 'primeng/table';
import { DragDropModule as CdkDragDropModule } from '@angular/cdk/drag-drop'
import { GermplasmListCreationComponent } from './list-creation/germplasm-list-creation.component';
import { SampleListBuilderService } from './list-creation/service/sample-list-builder.service';
import { GermplasmListBuilderService } from './list-creation/service/germplasm-list-builder.service';
import { SampleListCreationComponent } from './list-creation/sample-list-creation.component';
import { KeySequenceRegisterService } from './key-sequence-register/service/key-sequence-register.service';
import { ProgramService } from './program/service/program.service';
import { ToolService } from './tool/service/tool.service';

@NgModule({
    imports: [
        BmsjHipsterSharedLibsModule,
        BmsjHipsterSharedCommonModule,
        ReactiveFormsModule,
        TableModule,
        TreeModule,
        DragDropModule,
        CdkDragDropModule
    ],
    declarations: [
        ModalComponent,
        ModalConfirmComponent,
        HasAnyAuthorityDirective,
        HasNotAnyAuthorityDirective,
        CustomMinGreaterThanValidatorDirective,
        CustomMinEqualsValidatorDirective,
        ColumnFilterNumberComponent,
        ColumnFilterNumberRangeComponent,
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
        ColumnFilterAttributesComponent,
        ColumnFilterNameTypesComponent,
        KeyValuePipe,
        BreedingMethodSelect2DataPipe,
        ItemCountCustomComponent,
        LocationSelect2DataPipe,
        ListBuilderComponent,
        GermplasmListCreationComponent,
        SampleListCreationComponent
    ],
    providers: [
        LoginService,
        DatePipe,
        KeyValuePipe,
        Principal,
        AccountService,
        InventoryService,
        LotService,
        TransactionService,
        ParamContext,
        ListBuilderContext,
        GermplasmListBuilderService,
        SampleListBuilderService,
        HelpService,
        GermplasmService,
        AttributesService,
        NameTypeService,
        BreedingMethodService,
        LocationService,
        ProgramService,
        PopupService,
        AlertService,
        ToolService,
        KeySequenceRegisterService,
        /*
         * Workaround to reuse modal content outside ngb modals
         * https://github.com/ng-bootstrap/ng-bootstrap/issues/1755#issuecomment-344088034
         */
        NgbActiveModal
    ],
    entryComponents: [
        ModalComponent,
        ModalConfirmComponent,
        GermplasmListCreationComponent,
        SampleListCreationComponent
    ],
    exports: [
        BmsjHipsterSharedCommonModule,
        SharedModule,
        TreeTableModule,
        TreeModule,
        DragDropModule,
        CdkDragDropModule,
        TableModule,
        DatePipe,
        KeyValuePipe,
        BreedingMethodSelect2DataPipe,
        ModalComponent,
        ModalConfirmComponent,
        HasAnyAuthorityDirective,
        HasNotAnyAuthorityDirective,
        CustomMinGreaterThanValidatorDirective,
        CustomMinEqualsValidatorDirective,
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
        ColumnFilterAttributesComponent,
        ColumnFilterNameTypesComponent,
        ItemCountCustomComponent,
        LocationSelect2DataPipe,
        ListBuilderComponent,
        GermplasmListCreationComponent,
        SampleListCreationComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {
}
