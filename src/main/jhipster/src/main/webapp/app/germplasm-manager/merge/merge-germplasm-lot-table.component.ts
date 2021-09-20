import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Lot } from '../../shared/inventory/model/lot.model';
import { NonSelectedGermplasm } from '../../shared/germplasm/model/germplasm-merge-request.model';
import { LotMergeOptionsEnum } from './merge-germplasm-existing-lots.component';

@Component({
    selector: 'jhi-merge-germplasm-lot-table',
    templateUrl: './merge-germplasm-lot-table.component.html'
})
export class MergeGermplasmLotTableComponent implements OnChanges {

    lotMergeOptionsEnum = LotMergeOptionsEnum;

    @Input() lots: Lot[] = [];
    @Input() nonSelectedGermplasm: NonSelectedGermplasm;
    @Input() applyToAll: LotMergeOptionsEnum = LotMergeOptionsEnum.CLOSE;
    @Output() applyToAllChange: EventEmitter<LotMergeOptionsEnum> = new EventEmitter<LotMergeOptionsEnum>();

    selectedOption: LotMergeOptionsEnum;

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
        this.nonSelectedGermplasm.closeLots = this.selectedOption === LotMergeOptionsEnum.CLOSE;
        this.nonSelectedGermplasm.migrateLots = this.selectedOption === LotMergeOptionsEnum.MIGRATE;
        this.nonSelectedGermplasm.omit = this.selectedOption === LotMergeOptionsEnum.OMIT;
    }

}
