import { Component, Input, OnInit } from '@angular/core';
import { GermplasmListDataSearchResponse } from '../shared/germplasm-list/model/germplasm-list-data-search-response.model';
import { ColumnAlias } from './list.component';
import { GermplasmListObservationVariable } from '../shared/germplasm-list/model/germplasm-list-observation-variable.model';
import { GermplasmListColumnCategory } from '../shared/germplasm-list/model/germplasm-list-column-category.type';
import { InlineEditorService } from '../shared/inline-editor/inline-editor.service';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { JhiEventManager } from 'ng-jhipster';
import { GermplasmList } from '../shared/germplasm-list/model/germplasm-list.model';
import { VariableValidationService } from '../shared/ontology/service/variable-validation.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalConfirmComponent } from '../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-list-data-row',
    templateUrl: './list-table-row-data.component.html'
})
export class ListDataRowComponent implements OnInit {

    @Input() listId: number;
    @Input() germplasmList: GermplasmList;
    @Input() column: GermplasmListObservationVariable;
    @Input() entry: GermplasmListDataSearchResponse;

    private readonly LOCATION_ID = 'LOCATION_ID';
    private readonly BREEDING_METHOD_ID = 'BREEDING_METHOD_ID';

    constructor(
        private inlineEditorService: InlineEditorService,
        private germplasmListService: GermplasmListService,
        private eventManager: JhiEventManager,
        private alertService: AlertService,
        private validationService: VariableValidationService,
        private modalService: NgbModal,
        private translateService: TranslateService
    ) {
    }

    ngOnInit(): void {
    }

    get rowData() {
        if (this.column.columnCategory === GermplasmListColumnCategory.STATIC) {
            return this.entry.data[this.column.alias];
        }

        return this.entry.data[this.column.columnCategory + '_' + this.column.termId] ;
    }

    set rowData(rowData: string) {
        if (this.column.columnCategory === GermplasmListColumnCategory.STATIC) {
            this.entry.data[this.column.alias] = rowData;
        } else {
            this.entry.data[this.column.columnCategory + '_' + this.column.termId] = rowData;
        }
    }

    get observationId() {
        return this.entry.data['VARIABLE_' + this.column.termId + '_DETAIL_ID'];
    }

    set observationId(observationId: number) {
        this.entry.data['VARIABLE_' + this.column.termId + '_DETAIL_ID'] = observationId;
    }

    getGidData() {
        return this.entry.data[ColumnAlias.GID];
    }

    getLocationIdData() {
        return this.entry.data[this.LOCATION_ID];
    }

    getBreedingMethodIdData() {
        return this.entry.data[this.BREEDING_METHOD_ID];
    }

    shouldHasLink() {
        return this.shouldHasGidDetailsLink() ||
            this.isDesignationColumn() ||
            this.shouldHasLotsLink() ||
            this.isLocationNameColumn() ||
            this.isBreedingMethodNameColumn();
    }

    isGidColumn(): boolean {
        return this.column.alias === ColumnAlias.GID;
    }

    isDesignationColumn(): boolean {
        return this.column.alias === ColumnAlias.DESIGNATION;
    }

    isLotsColumn(): boolean {
        return this.column.alias === ColumnAlias.LOTS;
    }

    isGroupIdColumn(): boolean {
        return this.column.alias === ColumnAlias.GROUP_ID;
    }

    isLocationNameColumn(): boolean {
        return this.column.alias === ColumnAlias.LOCATION_NAME;
    }

    isBreedingMethodNameColumn(): boolean {
        return this.column.alias === ColumnAlias.BREEDING_METHOD_PREFERRED_NAME;
    }

    shouldHasGidDetailsLink(): boolean {
        return this.isGidColumn() || (this.isGroupIdColumn() && Boolean(this.rowData));
    }

    shouldHasLotsLink(): boolean {
        return this.isLotsColumn() && Boolean(this.rowData);
    }

    isEditable() {
        return this.column.columnCategory === GermplasmListColumnCategory.VARIABLE;
    }

    edit() {
        this.inlineEditorService.editingEntry = [this.entry.listDataId.toString(), this.column.termId.toString()];
    }

    isEditing() {
        return this.inlineEditorService.editingEntry[0] === this.entry.listDataId.toString()
            && this.inlineEditorService.editingEntry[1] === this.column.termId.toString();
    }

    async submit(value) {
        this.inlineEditorService.editingEntry = null;
        try {
            await this.validateNumericRange(value);
        } catch (e) {
            // (out of range -> cancel) would not go back to inline edition because blur would trigger again
            return;
        }
        if (this.observationId) {
            if (value || value === 0) {
                if (value === this.rowData) {
                    return;
                }
                this.germplasmListService.modifyObservation(this.listId, value, this.observationId)
                    .subscribe(
                        () => this.rowData = value,
                        (error) => this.onError(error)
                    );
            } else {
                this.germplasmListService.removeObservation(this.listId, this.observationId)
                    .subscribe(
                        () => {
                            this.rowData = '';
                            this.observationId = null;
                        },
                        (error) => this.onError(error)
                    );
            }
        } else {
            if (!(value || value === 0)) {
                return;
            }
            this.germplasmListService.createObservation(this.listId, this.entry.listDataId, this.column.termId, value)
                .subscribe(
                    (resp) => {
                        this.rowData = value;
                        this.observationId = resp.body;
                    },
                    (error) => this.onError(error)
                );
        }
    }

    cancel() {
        this.inlineEditorService.editingEntry = null;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }

    private async validateNumericRange(value) {
        if (!(value || value === 0) || value === this.rowData) {
            return;
        }
        const isOutOfrange = !this.validationService.isValidValueObservation(value, this.column).isInRange;
        if (isOutOfrange) {
            const min = (this.column.variableMinRange || this.column.variableMinRange === 0)
                ? this.column.variableMinRange
                : this.column.scaleMinRange;
            const max = (this.column.variableMaxRange || this.column.variableMaxRange === 0)
                ? this.column.variableMaxRange
                : this.column.scaleMaxRange;
            const confirmModal = this.modalService.open(ModalConfirmComponent);
            confirmModal.componentInstance.message = this.translateService.instant('germplasm-list.list-data.out.of.range.warning', {value, min, max});
            return confirmModal.result;
        }
    }
}
