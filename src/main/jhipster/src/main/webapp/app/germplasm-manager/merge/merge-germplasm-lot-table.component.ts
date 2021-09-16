import { Component, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { Lot } from '../../shared/inventory/model/lot.model';
import { NonSelectedGermplasm } from '../../shared/germplasm/model/germplasm-merge-request.model';

@Component({
    selector: 'jhi-merge-germplasm-lot-table',
    templateUrl: './merge-germplasm-lot-table.component.html'
})
export class MergeGermplasmLotTableComponent implements OnChanges {

    readonly LOT_OPTIONS_OMIT = 'omit';
    readonly LOT_OPTIONS_MIGRATE = 'migrate';
    readonly LOT_OPTIONS_CLOSE = 'close';

    @Input() lots: Lot[] = [];
    @Input() nonSelectedGermplasm: NonSelectedGermplasm;
    @Input() applyToAll: string;
    @Output() applyToAllChange: EventEmitter<string> = new EventEmitter<string>();

    selectedOption;

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
        this.nonSelectedGermplasm.closeLots = this.selectedOption === this.LOT_OPTIONS_CLOSE;
        this.nonSelectedGermplasm.migrateLots = this.selectedOption === this.LOT_OPTIONS_MIGRATE;
        this.nonSelectedGermplasm.omit = this.selectedOption === this.LOT_OPTIONS_OMIT;
    }

}
