import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Lot } from '../../shared/inventory/model/lot.model';
import { NonSelectedGermplasm } from '../../shared/germplasm/model/germplasm-merge-request.model';
import { LotMergeOptionsEnum } from './merge-germplasm-existing-lots.component';
import { LotSearch } from '../../shared/inventory/model/lot-search.model';
import { finalize } from 'rxjs/operators';
import { LotService } from '../../shared/inventory/service/lot.service';

@Component({
    selector: 'jhi-merge-germplasm-lot-table',
    templateUrl: './merge-germplasm-lot-table.component.html'
})
export class MergeGermplasmLotTableComponent implements OnChanges, OnInit {

    lotMergeOptionsEnum = LotMergeOptionsEnum;
    isLoading: boolean;
    lots: Lot[] = [];
    itemsPerPage: any = 10;
    page: any = 1;
    previousPage: any;
    filteredItems: any;

    @Input() nonSelectedGermplasm: NonSelectedGermplasm;
    @Input() applyToAll: LotMergeOptionsEnum = LotMergeOptionsEnum.CLOSE;
    @Output() applyToAllChange: EventEmitter<LotMergeOptionsEnum> = new EventEmitter<LotMergeOptionsEnum>();

    selectedOption: LotMergeOptionsEnum;

    constructor(
        private lotService: LotService) {
    }

    ngOnInit(): void {
        this.transition();
    }

    onLotOptionChanged() {
        this.updateNonSelectedGermplasmOptions();
        this.applyToAllChange.emit(null);
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['applyToAll'] && changes['applyToAll'].currentValue) {
            const option = changes['applyToAll'].currentValue;
            this.selectedOption = option;
            this.updateNonSelectedGermplasmOptions();
        }
    }

    updateNonSelectedGermplasmOptions() {
        this.nonSelectedGermplasm.migrateLots = this.selectedOption === LotMergeOptionsEnum.MIGRATE;
        this.nonSelectedGermplasm.omit = this.selectedOption === LotMergeOptionsEnum.OMIT;
    }

    search(request: LotSearch): Promise<string> {
        return new Promise((resolve, reject) => {
            this.lotService.search(request).subscribe((response) => {
                resolve(response);
            }, (error) => reject(error));
        });
    }

    loadLots(request: LotSearch) {
        this.isLoading = true;
        this.search(request).then((searchId) => {
            this.lotService.getSearchResults({
                searchRequestId: searchId,
                page: this.page - 1,
                size: this.itemsPerPage
            }).pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe((response) => {
                    this.filteredItems = response.headers.get('X-Filtered-Count');
                    this.lots = response.body;
                }
            );
        });
    }

    transition() {
        const lotSearch = new LotSearch();
        lotSearch.gids = [this.nonSelectedGermplasm.germplasmId.toString()];
        // Get lot records
        this.loadLots(lotSearch);
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

}
