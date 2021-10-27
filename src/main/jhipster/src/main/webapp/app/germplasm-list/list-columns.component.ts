import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { GermplasmListColumn } from '../shared/germplasm-list/model/germplasm-list-column.model';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';
import { NgbDropdown } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListColumnCategory } from '../shared/germplasm-list/model/germplasm-list-column-category.type';
import { TermIdEnum } from '../shared/ontology/model/termid.enum';
import { MANAGE_GERMPLASM_LIST_PERMISSIONS } from '../shared/auth/permissions';
import { GERMPLASM_LIST_LABEL_PRINTING_TYPE } from '../app.constants';
import { ParamContext } from '../shared/service/param.context';

@Component({
    selector: 'jhi-list-columns',
    templateUrl: './list-columns.component.html',
    styleUrls: [
        './list-columns.component.scss'
    ]
})
export class ListColumnsComponent implements OnInit {

    @Input() listId: number;

    @Output() columnsSelectedEvent = new EventEmitter<GermplasmListColumnModel[]>();

    @ViewChild('columnsDropdown') columnsDropdown: NgbDropdown;

    ACTION_BUTTON_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSIONS];
    GERMPLASM_LIST_LABEL_PRINTING_PERMISSIONS = [...MANAGE_GERMPLASM_LIST_PERMISSIONS, 'GERMPLASM_LIST_LABEL_PRINTING'];

    TermIdEnum = TermIdEnum;

    staticColumns: GermplasmListColumnModel[] = [];
    nameColumns: GermplasmListColumnModel[] = [];
    passportColumns: GermplasmListColumnModel[] = [];
    attributesColumns: GermplasmListColumnModel[] = [];

    filteredStaticColumns: GermplasmListColumnModel[] = [];
    filteredNameColumns: GermplasmListColumnModel[] = [];
    filteredPassportColumns: GermplasmListColumnModel[] = [];
    filteredAttributesColumns: GermplasmListColumnModel[] = [];

    constructor(private germplasmListService: GermplasmListService,
                private alertService: AlertService,
                private paramContext: ParamContext) {
    }

    ngOnInit(): void {
        this.germplasmListService.getGermplasmListColumns(this.listId).subscribe(
            (res: HttpResponse<GermplasmListColumn[]>) => this.onGetColumnsSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    apply() {
        this.columnsDropdown.close();

        const selectedColumns: GermplasmListColumnModel[] = this.getSelectedColumns();
        this.columnsSelectedEvent.emit(selectedColumns);
    }

    onSearchColumn(evt: any) {
        const searchString = evt.target.value.toLowerCase();

        if (searchString) {
            this.filteredStaticColumns = this.filterColumns(this.staticColumns, searchString);
            this.filteredNameColumns = this.filterColumns(this.nameColumns, searchString);
            this.filteredPassportColumns = this.filterColumns(this.passportColumns, searchString);
            this.filteredAttributesColumns = this.filterColumns(this.attributesColumns, searchString);
        } else {
            this.resetColumns();
        }
    }
    exportDataAndLabels() {
        this.paramContext.resetQueryParams().then(() => {
            /*
             * FIXME workaround for history.back() with base-href
             *  Find solution for IBP-3534 / IBP-4177 that doesn't involve base-href
             *  or 'inventory-manager' string
             */
            window.history.pushState({}, '', window.location.hash);

            window.location.href = '/ibpworkbench/controller/jhipster#label-printing'
                + '?cropName=' + this.paramContext.cropName
                + '&programUUID=' + this.paramContext.programUUID
                + '&printingLabelType=' + GERMPLASM_LIST_LABEL_PRINTING_TYPE
                + '&listId=' + this.listId;
        });
    }

    private getSelectedColumns(): GermplasmListColumnModel[] {
        return [].concat(this.filteredStaticColumns, this.filteredNameColumns, this.filteredPassportColumns, this.filteredAttributesColumns)
            .filter((column: GermplasmListColumnModel) => column.selected);
    }

    private filterColumns(columns: GermplasmListColumnModel[], searchString: string): GermplasmListColumnModel[] {
        return columns.filter((column: GermplasmListColumnModel) => column.displayName.toLowerCase().includes(searchString));
    }

    private onGetColumnsSuccess(columns: GermplasmListColumn[]) {
        const columnModels = this.transformGermplasmListColumns(columns);

        columnModels.forEach((column: GermplasmListColumnModel) => {
            if (column.category === GermplasmListColumnCategory.STATIC) {
                this.staticColumns.push(column);
            }
            if (column.category === GermplasmListColumnCategory.NAMES) {
                this.nameColumns.push(column);
            }
            if (column.category === GermplasmListColumnCategory.VARIABLE) {
                if (column.typeId === VariableTypeEnum.GERMPLASM_PASSPORT) {
                    this.passportColumns.push(column);
                }
                if (column.typeId === VariableTypeEnum.GERMPLASM_ATTRIBUTE) {
                    this.attributesColumns.push(column);
                }
            }
        });

        this.resetColumns();
    }

    private transformGermplasmListColumns(columns: GermplasmListColumn[]): GermplasmListColumnModel[] {
        return columns.map((column: GermplasmListColumn) =>
            new GermplasmListColumnModel(column.id,
                column.alias ? `${column.alias} (${column.name}) ` : column.name,
                column.category,
                column.selected,
                column.typeId));
    }

    private resetColumns() {
        this.filteredStaticColumns = this.staticColumns;
        this.filteredNameColumns = this.nameColumns;
        this.filteredPassportColumns = this.passportColumns;
        this.filteredAttributesColumns = this.attributesColumns;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

}

export class GermplasmListColumnModel {

    constructor(
        public id: number,
        public displayName: string,
        public category: GermplasmListColumnCategory,
        public selected: boolean,
        public typeId?: number) {
    }

}
