import { Component, Input, OnInit } from '@angular/core';
import { GermplasmListDataSearchResponse } from '../shared/germplasm-list/model/germplasm-list-data-search-response.model';
import { ObservationVariable } from '../shared/model/observation-variable.model';
import { ColumnLabels } from './list.component';

@Component({
    selector: 'jhi-list-data-row',
    templateUrl: './list-table-row-data.component.html'
})
export class ListDataRowComponent implements OnInit {

    @Input() column: ObservationVariable;
    @Input() entry: GermplasmListDataSearchResponse;

    private readonly LOCATION_ID = 'LOCATION_ID';
    private readonly BREEDING_METHOD_ID = 'BREEDING_METHOD_ID';

    constructor() {
    }

    ngOnInit(): void {
    }

    getRowData() {
        return this.entry.data[this.column.alias] === undefined ? this.entry.data[this.column.termId] : this.entry.data[this.column.alias];
    }

    getGidData() {
        return this.entry.data[ColumnLabels.GID];
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
        return this.column.alias === ColumnLabels.GID;
    }

    isLotsColumn() {
        return this.column.alias === ColumnLabels.LOTS;
    }

    isLocationNameColumn() {
        return this.column.alias === ColumnLabels.LOCATION_NAME;
    }

    isBreedingMethodNameColumn() {
        return this.column.alias === ColumnLabels.BREEDING_METHOD_PREFERRED_NAME;
    }

}
