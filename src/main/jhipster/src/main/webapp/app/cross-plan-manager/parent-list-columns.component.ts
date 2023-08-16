import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NgbDropdown} from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-parent-list-columns',
    templateUrl: './parent-list-columns.component.html',
    styleUrls: [
        './parent-list-columns.component.scss'
    ]
})
export class ParentListColumnsComponent implements OnInit {

    @Output() columnsSelectedEvent = new EventEmitter<ParentListColumnModel[]>();

    @ViewChild('columnsDropdown') columnsDropdown: NgbDropdown;

    @Input() defaultColumns: ParentListColumnModel[] = [];
    @Input() nameColumns: ParentListColumnModel[] = [];
    @Input() passportColumns: ParentListColumnModel[] = [];
    @Input() attributesColumns: ParentListColumnModel[] = [];

    constructor() {

    }

    ngOnInit(): void {
    }

    dropdownToggle() {
        if (this.columnsDropdown._open) {
            this.load();
        }
    }

    private load() {

    }

    onSearchColumn(evt: any) {
        const searchString = evt.target.value.toLowerCase();

        if (searchString) {
            this.filterColumns(this.concatAllColumns(), searchString);
        } else {
            this.resetColumns();
        }
    }

    private concatAllColumns(): ParentListColumnModel[] {
        return [].concat(this.defaultColumns, this.nameColumns, this.passportColumns, this.attributesColumns);
    }

    private resetColumns() {
        this.concatAllColumns().forEach((column: ParentListColumnModel) => column.visible = true);
    }

    private filterColumns(columns: ParentListColumnModel[], searchString: string) {
        columns.forEach((column: ParentListColumnModel) => {
            if (!column.name.toLowerCase().includes(searchString)) {
                column.visible = false;
            }
        });
    }

    checkAreColumnsVisible(columns: ParentListColumnModel[]): boolean {
        return columns.filter((column: ParentListColumnModel) => column.visible).length > 0;
    }

    private getSelectedColumns(): ParentListColumnModel[] {
        return this.concatAllColumns();
    }

    apply() {
        this.columnsDropdown.close();

        const selectedColumns: ParentListColumnModel[] = this.getSelectedColumns();
        this.columnsSelectedEvent.emit(selectedColumns);
    }
}

export class ParentListColumnModel {

    constructor(
        public name: String,
        public category: ParentListColumnCategory,
        public selected: boolean,
        public visible = true
    ) {
    }
}

export enum ParentListColumnCategory {
    DEFAULT = 'DEFAULT',
    NAMES = 'NAMES',
    ATTRIBUTES = 'ATTRIBUTES',
    PASSPORT = 'PASSPORT',
}
