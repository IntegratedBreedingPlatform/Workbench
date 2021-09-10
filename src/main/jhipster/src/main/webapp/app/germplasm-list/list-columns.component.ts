import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { GermplasmListColumn, GermplasmListColumnCategory } from '../shared/germplasm-list/model/germplasm-list-column.model';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';
import { NgbDropdown } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-list-columns',
    templateUrl: './list-columns.component.html',
    styleUrls: [
        './germplasm-list.scss'
    ]
})
export class ListColumnsComponent implements OnInit {

    @Input() listId: number;

    @Output() columnsSelectedEvent = new EventEmitter<number[]>();

    @ViewChild('columnsDropdown') columnsDropdown: NgbDropdown;

    readonly ENTRY_NO_TERM_ID = 8230;

    staticColumns: GermplasmListColumn[] = [];
    nameColumns: GermplasmListColumn[] = [];
    passportColumns: GermplasmListColumn[] = [];
    attributesColumns: GermplasmListColumn[] = [];

    filteredStaticColumns: GermplasmListColumn[] = [];
    filteredNameColumns: GermplasmListColumn[] = [];
    filteredPassportColumns: GermplasmListColumn[] = [];
    filteredAttributesColumns: GermplasmListColumn[] = [];

    constructor(private germplasmListService: GermplasmListService,
                private alertService: AlertService) {
    }

    ngOnInit(): void {
        this.germplasmListService.getGermplasmListColumns(this.listId).subscribe(
            (res: HttpResponse<GermplasmListColumn[]>) => this.onGetColumnsSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    apply() {
        this.columnsDropdown.close();

        const selectedColumnIds: number[] = this.getSelectedColumnIds();
        this.columnsSelectedEvent.emit(selectedColumnIds);
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

    private getSelectedColumnIds(): number[] {
        return [].concat(this.filteredStaticColumns, this.filteredNameColumns, this.filteredPassportColumns, this.filteredAttributesColumns)
            .filter((column: GermplasmListColumn) => column.selected)
            .map((column: GermplasmListColumn) => column.id);
    }

    private filterColumns(columns: GermplasmListColumn[], searchString: string): GermplasmListColumn[] {
        return columns.filter((column: GermplasmListColumn) => column.name.toLowerCase().includes(searchString));
    }

    private onGetColumnsSuccess(columns: GermplasmListColumn[]) {
        columns.forEach((column: GermplasmListColumn) => {
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
