import { Component, OnInit } from '@angular/core';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmImportContext } from './germplasm-import.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-import-matches',
    templateUrl: 'germplasm-import-matches.component.html'
})
export class GermplasmImportMatchesComponent implements OnInit {

    page = 0;
    pageSize = 10;

    // modal input
    unassignedMatches: any[] = [];
    matchesByName: { [key: string]: GermplasmDto };

    unassignedCount: number;
    matches: GermplasmDto[] = [];
    dataRow: any;

    constructor(
        private context: GermplasmImportContext,
        private modal: NgbActiveModal
    ) {
    }

    ngOnInit(): void {
        this.unassignedCount = this.unassignedMatches.length;
        this.dataRow = this.unassignedMatches[0];
        this.matches = this.context.nametypesCopy
            .filter((nameType) => Boolean(this.matchesByName[this.dataRow[nameType.code]]))
            .map((nameType) => this.matchesByName[this.dataRow[nameType.code]]);

    }

    dismiss() {
        this.modal.dismiss();
    }

    next() {
        // finish
        // this.modal.close();
    }
}
