import { Component, Input, OnInit } from '@angular/core';
import { GermplasmListDataSearchResponse } from '../shared/germplasm-list/model/germplasm-list-data-search-response.model';
import { ColumnAlias, ListComponent } from './list.component';
import { GermplasmListObservationVariable } from '../shared/germplasm-list/model/germplasm-list-observation-variable.model';
import { GermplasmListColumnCategory } from '../shared/germplasm-list/model/germplasm-list-column-category.type';
import { InlineEditorService } from '../shared/inline-editor/inline-editor.service';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { JhiEventManager } from 'ng-jhipster';

@Component({
    selector: 'jhi-list-data-row',
    templateUrl: './list-table-row-data.component.html'
})
export class ListDataRowComponent implements OnInit {

    @Input() listId: number;
    @Input() column: GermplasmListObservationVariable;
    @Input() entry: GermplasmListDataSearchResponse;

    private readonly LOCATION_ID = 'LOCATION_ID';
    private readonly BREEDING_METHOD_ID = 'BREEDING_METHOD_ID';

    constructor(
        private inlineEditorService: InlineEditorService,
        private germplasmListService: GermplasmListService,
        private eventManager: JhiEventManager,
        private alertService: AlertService
    ) {
    }

    ngOnInit(): void {
    }

    getRowData() {
        if (this.column.columnCategory === GermplasmListColumnCategory.STATIC) {
            return this.entry.data[this.column.alias];
        }

        return this.entry.data[this.column.columnCategory + '_' + this.column.termId] ;
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
        return this.isGidColumn() ||
            this.isLotsColumn() ||
            this.isLocationNameColumn() ||
            this.isBreedingMethodNameColumn();
    }

    isGidColumn() {
        return this.column.alias === ColumnAlias.GID;
    }

    isLotsColumn() {
        return this.column.alias === ColumnAlias.LOTS;
    }

    isLocationNameColumn() {
        return this.column.alias === ColumnAlias.LOCATION_NAME;
    }

    isBreedingMethodNameColumn() {
        return this.column.alias === ColumnAlias.BREEDING_METHOD_PREFERRED_NAME;
    }

    edit() {
        this.inlineEditorService.editingEntry = [this.entry.listDataId.toString(), this.column.termId.toString()];
    }

    isEditing() {
        return this.inlineEditorService.editingEntry[0] === this.entry.listDataId.toString()
            && this.inlineEditorService.editingEntry[1] === this.column.termId.toString();
    }

    submit(value) {
        this.inlineEditorService.editingEntry = null;
        const observationId = this.entry.data['VARIABLE_' + this.column.termId + '_DETAIL_ID'];
        if (observationId) {
            if (value) {
                if (value === this.getRowData()) {
                    return;
                }
                this.germplasmListService.modifyObservation(this.listId, value, observationId)
                    .subscribe(
                        () => this.onSuccess(),
                        (error) => this.onError(error)
                    );
            } else {
                this.germplasmListService.removeObservation(this.listId, observationId)
                    .subscribe(
                        () => this.onSuccess(),
                        (error) => this.onError(error)
                    );
            }
        } else {
            if (!value && value !== 0) {
                return;
            }
            this.germplasmListService.createObservation(this.listId, this.entry.listDataId, this.column.termId, value)
                .subscribe(
                    () => this.onSuccess(),
                    (error) => this.onError(error)
                );
        }
    }

    cancel() {
        this.inlineEditorService.editingEntry = null;
    }

    private onSuccess() {
        this.eventManager.broadcast({ name: this.listId + ListComponent.GERMPLASMLIST_VIEW_CHANGED_EVENT_SUFFIX, content: '' });
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }
}
