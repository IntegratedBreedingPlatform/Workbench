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
import { CollapsibleComponent } from './component/collapsible.component';
import { ProgramUsageService } from './service/program-usage.service';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { GeojsonMapComponent } from './geojson-map/geojson-map.component';
import { CropService } from './crop/service/crop.service';
import { CropSelect2DataPipe } from './crop/util/crop-select2.pipe';
import { UrlService } from './service/url.service';
import { LocationsSelectComponent } from './locations-select/locations-select.component';
import { NameTypeSelect2Pipe } from './name-type/model/name-type-select2.pipe';
import { DateHelperService } from './service/date.helper.service';
import { AttributeSelect2DataPipe } from './attributes/model/attribute-select2.pipe';
import { OnlyNumbersDirective } from './util/apply-only-numbers.directive';
import { SecureImagePipe } from './util/secure-image.pipe';
import { GermplasmPedigreeService } from './germplasm/service/germplasm.pedigree.service';
import { VariableSelectComponent } from './variable-select/variable-select.component';
import { PedigreeGraphComponent } from './pedigree-graph/pedigree-graph.component';
import { VariableService } from './ontology/service/variable.service';
import { GermplasmGroupingService } from './germplasm/service/germplasm-grouping.service';
import { VariableValidationService } from './ontology/service/variable-validation.service';
import { FileService } from './file/service/file.service';
import { ColumnFilterInlineComponent } from './column-filter/inline/column-filter-inline.component';
import { ColumnFilterDropdownComponent } from './column-filter/column-filter-dropdown-component';
import { InlineEditorComponent } from './inline-editor/inline-editor.component';
import { InlineEditorService } from './inline-editor/inline-editor.service';
import { VariableContainerComponent } from './variable-container/variable-container.component';
import { VariableSelectModalComponent } from './variable-container/variable-select-modal.component';
import { FileDeleteOptionsComponent } from './file/component/file-delete-options.component';
import { RouterModule } from '@angular/router';
import { GermplasmListCloneComponent } from './list-creation/germplasm-list-clone.component';
import { FeedbackDialogComponent } from './feedback/feedback-dialog.component';
import { FeedbackService } from './feedback/service/feedback.service';
import { GermplasmTreeTableComponent } from './tree/germplasm/germplasm-tree-table.component';
import { GermplasmListTreeTableComponent } from './tree/germplasm/germplasm-list-tree-table.component';
import { StudyTreeComponent } from './tree/study/study-tree.component';
import { GermplasmListFolderSelectorComponent } from './tree/germplasm/germplasm-list-folder-selector.component';
import { MembersService } from './user/service/members.service';
import { RoleService } from './user/service/role.service';
import { ScrollableTooltipDirective } from './tooltip/scrollable-tooltip.directive';
import { TruncateWithEllipsisPipe } from './util/truncate-with-ellipsis.pipe';
import { CropParameterService } from './crop-parameter/service/crop-parameter.service';
import { GenotypingBrapiService } from './brapi/service/genotyping-brapi.service';
import { feedbackDialogRoutes } from './feedback/feedback-dialog.route';
import { StudyService } from './study/service/study.service';
import { EntryDetailsImportContext } from './ontology/entry-details-import.context';
import { EntryDetailsImportService } from './ontology/service/entry-details-import.service';
import { ModalAlertComponent } from './modal/modal-alert.component';
import { AdvanceService } from './study/service/advance.service';
import { ObservationVariableHelperService } from './dataset/model/observation-variable.helper.service';
import { ColumnFilterVariablesComponent } from './column-filter/column-filter-variables-component';
import { GenotypingParameterUtilService } from './genotyping/genotyping-parameter-util.service';
import { BreedingMethodsSelectComponent } from './breeding-methods-select/breeding-methods-select.component';

@NgModule({
    imports: [
        BmsjHipsterSharedLibsModule,
        BmsjHipsterSharedCommonModule,
        ReactiveFormsModule,
        TableModule,
        TreeModule,
        TreeTableModule,
        DragDropModule,
        CdkDragDropModule,
        LeafletModule,
        RouterModule,
        RouterModule.forChild([...feedbackDialogRoutes]),
    ],
    declarations: [
        ModalComponent,
        ModalConfirmComponent,
        ModalAlertComponent,
        FileDeleteOptionsComponent,
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
        ColumnFilterInlineComponent,
        ColVisButtonComponent,
        ColumnFilterBooleanComponent,
        ColumnFilterTextWithMatchOptionsComponent,
        ColumnFilterPedigreeOptionsComponent,
        ColumnFilterAttributesComponent,
        ColumnFilterNameTypesComponent,
        ColumnFilterDropdownComponent,
        ColumnFilterVariablesComponent,
        KeyValuePipe,
        SecureImagePipe,
        CropSelect2DataPipe,
        NameTypeSelect2Pipe,
        AttributeSelect2DataPipe,
        TruncateWithEllipsisPipe,
        ItemCountCustomComponent,
        LocationSelect2DataPipe,
        GermplasmTreeTableComponent,
        GermplasmListTreeTableComponent,
        StudyTreeComponent,
        GermplasmListFolderSelectorComponent,
        ListBuilderComponent,
        GermplasmListCreationComponent,
        GermplasmListCloneComponent,
        SampleListCreationComponent,
        CollapsibleComponent,
        GeojsonMapComponent,
        LocationsSelectComponent,
        BreedingMethodsSelectComponent,
        OnlyNumbersDirective,
        PedigreeGraphComponent,
        VariableSelectComponent,
        InlineEditorComponent,
        VariableSelectComponent,
        VariableSelectModalComponent,
        VariableContainerComponent,
        FeedbackDialogComponent,
        ScrollableTooltipDirective
    ],
    providers: [
        LoginService,
        DatePipe,
        KeyValuePipe,
        SecureImagePipe,
        Principal,
        AccountService,
        MembersService,
        RoleService,
        InventoryService,
        LotService,
        TransactionService,
        ParamContext,
        ListBuilderContext,
        GermplasmListBuilderService,
        SampleListBuilderService,
        HelpService,
        GermplasmService,
        BreedingMethodService,
        LocationService,
        ProgramService,
        CropService,
        CropParameterService,
        PopupService,
        AlertService,
        ToolService,
        KeySequenceRegisterService,
        ProgramUsageService,
        DateHelperService,
        /*
         * Workaround to reuse modal content outside ngb modals
         * https://github.com/ng-bootstrap/ng-bootstrap/issues/1755#issuecomment-344088034
         */
        NgbActiveModal,
        FileService,
        UrlService,
        GermplasmPedigreeService,
        VariableService,
        EntryDetailsImportService,
        VariableValidationService,
        StudyService,
        GermplasmGroupingService,
        InlineEditorService,
        FeedbackService,
        EntryDetailsImportContext,
        FeedbackService,
        GenotypingBrapiService,
        AdvanceService,
        GenotypingBrapiService,
        ObservationVariableHelperService,
        GenotypingParameterUtilService
    ],
    entryComponents: [
        ModalComponent,
        ModalConfirmComponent,
        ModalAlertComponent,
        FileDeleteOptionsComponent,
        GermplasmTreeTableComponent,
        GermplasmListTreeTableComponent,
        StudyTreeComponent,
        GermplasmListFolderSelectorComponent,
        GermplasmListCreationComponent,
        GermplasmListCloneComponent,
        SampleListCreationComponent,
        VariableSelectModalComponent,
        FeedbackDialogComponent
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
        SecureImagePipe,
        CropSelect2DataPipe,
        NameTypeSelect2Pipe,
        AttributeSelect2DataPipe,
        TruncateWithEllipsisPipe,
        ModalComponent,
        ModalConfirmComponent,
        ModalAlertComponent,
        FileDeleteOptionsComponent,
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
        ColumnFilterInlineComponent,
        ColVisButtonComponent,
        ColumnFilterBooleanComponent,
        ColumnFilterTextWithMatchOptionsComponent,
        ColumnFilterPedigreeOptionsComponent,
        ColumnFilterAttributesComponent,
        ColumnFilterNameTypesComponent,
        ColumnFilterDropdownComponent,
        ColumnFilterVariablesComponent,
        ItemCountCustomComponent,
        LocationSelect2DataPipe,
        GermplasmTreeTableComponent,
        GermplasmListTreeTableComponent,
        StudyTreeComponent,
        GermplasmListFolderSelectorComponent,
        ListBuilderComponent,
        GermplasmListCreationComponent,
        GermplasmListCloneComponent,
        SampleListCreationComponent,
        CollapsibleComponent,
        GeojsonMapComponent,
        LocationsSelectComponent,
        BreedingMethodsSelectComponent,
        OnlyNumbersDirective,
        PedigreeGraphComponent,
        VariableSelectComponent,
        InlineEditorComponent,
        VariableSelectComponent,
        VariableSelectModalComponent,
        VariableContainerComponent,
        FeedbackDialogComponent,
        ScrollableTooltipDirective

    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BmsjHipsterSharedModule {
}
