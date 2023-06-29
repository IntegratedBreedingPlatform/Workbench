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

    @ViewChild('columnsDropdown', {static: true}) columnsDropdown: NgbDropdown;

    TermIdEnum = TermIdEnum;

    staticColumns: GermplasmListColumnModel[] = [];
    nameColumns: GermplasmListColumnModel[] = [];
    passportColumns: GermplasmListColumnModel[] = [];
    attributesColumns: GermplasmListColumnModel[] = [];

    constructor(private germplasmListService: GermplasmListService,
                private alertService: AlertService) {
    }

    ngOnInit(): void {
    }

    load() {
        this.staticColumns = [];
        this.nameColumns = [];
        this.passportColumns = [];
        this.attributesColumns = [];
        this.germplasmListService.getGermplasmListColumns(this.listId).subscribe(
            (res: HttpResponse<GermplasmListColumn[]>) => this.onGetColumnsSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    dropdownToggle() {
        if (this.columnsDropdown._open) {
            this.load();
        }
    }

    apply() {
        this.columnsDropdown.close();

        const selectedColumns: GermplasmListColumnModel[] = this.getSelectedColumns();
        this.columnsSelectedEvent.emit(selectedColumns);
    }

    onSearchColumn(evt: any) {
        const searchString = evt.target.value.toLowerCase();

        if (searchString) {
            this.filterColumns(this.concatAllColumns(), searchString);
        } else {
            this.resetColumns();
        }
    }

    checkAreColumnsVisible(columns: GermplasmListColumnModel[]): boolean {
        return columns.filter((column: GermplasmListColumnModel) => column.visible).length > 0;
    }

    private getSelectedColumns(): GermplasmListColumnModel[] {
        return this.concatAllColumns().filter((column: GermplasmListColumnModel) => column.selected);
    }

    private filterColumns(columns: GermplasmListColumnModel[], searchString: string) {
        columns.forEach((column: GermplasmListColumnModel) => {
           if (!column.displayName.toLowerCase().includes(searchString)) {
               column.visible = false;
           }
        });
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
        this.concatAllColumns().forEach((column: GermplasmListColumnModel) => column.visible = true);
    }

    private concatAllColumns(): GermplasmListColumnModel[] {
        return [].concat(this.staticColumns, this.nameColumns, this.passportColumns, this.attributesColumns);
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
        public typeId?: number,
        public visible = true) {
    }

}
