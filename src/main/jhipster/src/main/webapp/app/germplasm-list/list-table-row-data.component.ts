import { Component, Input, OnInit } from '@angular/core';
import { GermplasmListDataSearchResponse } from '../shared/germplasm-list/model/germplasm-list-data-search-response.model';
import { ColumnAlias } from './list.component';
import { GermplasmListObservationVariable } from '../shared/germplasm-list/model/germplasm-list-observation-variable.model';
import { GermplasmListColumnCategory } from '../shared/germplasm-list/model/germplasm-list-column-category.type';

@Component({
    selector: 'jhi-list-data-row',
    templateUrl: './list-table-row-data.component.html'
})
export class ListDataRowComponent implements OnInit {

    @Input() column: GermplasmListObservationVariable;
    @Input() entry: GermplasmListDataSearchResponse;

    private readonly LOCATION_ID = 'LOCATION_ID';
    private readonly BREEDING_METHOD_ID = 'BREEDING_METHOD_ID';

    constructor() {
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
        return this.isGidColumn() || (this.isGroupIdColumn() && this.getRowData());
    }

    shouldHasLotsLink(): boolean {
        return this.isLotsColumn() && this.getRowData();
    }

}
