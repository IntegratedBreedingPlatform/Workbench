import { Component, OnInit } from '@angular/core';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmImportContext } from './germplasm-import.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HEADERS } from './germplasm-import.component';

@Component({
    selector: 'jhi-germplasm-import-matches',
    templateUrl: 'germplasm-import-matches.component.html'
})
export class GermplasmImportMatchesComponent implements OnInit {

    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    // modal input
    unassignedMatches: any[] = [];
    matchesByName: { [key: string]: GermplasmDto };
    matchesByGUID: { [key: string]: GermplasmDto };

    // internal usage
    matchNumber = 1
    unassignedCount: number;
    matches: GermplasmDto[];
    dataRow: any = {};
    entryNo: string;
    name: string;
    guidMatch: GermplasmDto;
    isIgnoreAllRemaining: boolean;
    isIgnoreMatch: boolean;

    // ENTRY_NO -> gid or null (new)
    matchProcessResult: { [key: string]: number } = {};

    constructor(
        private context: GermplasmImportContext,
        private modal: NgbActiveModal
    ) {
    }

    ngOnInit(): void {
        this.unassignedCount = this.unassignedMatches.length;
        this.processMatch(this.matchNumber);

    }

    private processMatch(matchNumber) {
        this.isIgnoreMatch = false;

        this.dataRow = this.unassignedMatches[matchNumber - 1];
        this.guidMatch = this.matchesByGUID[this.dataRow[HEADERS.GUID]];
        this.matches = this.context.nametypesCopy
            .filter((nameType) => Boolean(this.matchesByName[this.dataRow[nameType.code]]))
            .map((nameType) => this.matchesByName[this.dataRow[nameType.code]])
            // dedup
            .filter((germplasm, i, array) => array.indexOf(germplasm) === i);
        this.entryNo = this.dataRow[HEADERS.ENTRY_NO];
    }

    onSelectMatch(germplasm: GermplasmDto) {
        this.matchProcessResult[this.dataRow[HEADERS.ENTRY_NO]] = germplasm.gid;
        this.isIgnoreMatch = false;
        this.isIgnoreAllRemaining = false;
    }

    onIgnoreMatch() {
        if (this.isIgnoreMatch) {
            this.matchProcessResult[this.dataRow[HEADERS.ENTRY_NO]] = null;
        }
    }

    onIgnoreAllRemaining() {
        if (this.isIgnoreAllRemaining) {
            // we consider also this match as "remaining"
            this.isIgnoreMatch = true;
            this.matchProcessResult[this.dataRow[HEADERS.ENTRY_NO]] = null;
        }
    }

    dismiss() {
        this.modal.dismiss();
    }

    back() {
        if (this.matchNumber === 1) {
            this.modal.dismiss();
        } else {
            this.processMatch(--this.matchNumber);
        }
    }

    next() {
        if (this.isFinish()) {
            this.modal.close(this.matchProcessResult);
            return;
        }
        this.processMatch(++this.matchNumber);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    private isFinish() {
        return this.matchNumber === this.unassignedCount
            || this.isIgnoreAllRemaining;
    }
}
