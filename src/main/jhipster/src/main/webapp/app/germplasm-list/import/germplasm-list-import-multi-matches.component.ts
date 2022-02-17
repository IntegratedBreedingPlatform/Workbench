import { Component, OnInit } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HEADERS } from './germplasm-list-import.component';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';

@Component({
    selector: 'jhi-germplasm-list-import-multi-matches',
    templateUrl: 'germplasm-list-import-multi-matches.component.html',
    animations: [
        trigger('tableAnimation', [
            transition('void => *', [
                style({ opacity: 0 }),
                animate(500)
            ]),
        ])
    ]
})
export class GermplasmListImportMultiMatchesComponent implements OnInit {

    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    selectMatchesResult: { [key: string]: number };
    // internal usage
    matchNumber = 1;
    unassignedCount: number;
    matches: GermplasmDto[] = [];
    dataRow: any = {};

    // modal input
    unassignedMatches: any[] = [];

    name: string;
    rowNumber: number;

    isIgnoreMatch: boolean;
    useSameMatchForAllOcurrences: boolean;
    selectedGermplasm: GermplasmDto;
    sameOccurrencesMap: { [key: string]: GermplasmDto } = {}; // Key preferred Name.

    constructor(
        private modal: NgbActiveModal
    ) {
    }

    ngOnInit(): void {
        this.useSameMatchForAllOcurrences = false;
        this.unassignedCount = this.unassignedMatches.length;
        this.processMatch(this.matchNumber);

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

    private processMatch(matchNumber) {
        this.page = 0;
        this.isIgnoreMatch = false;
        this.dataRow = this.unassignedMatches[matchNumber - 1];
        const germplasm = this.sameOccurrencesMap[this.dataRow[HEADERS.DESIGNATION]];
        if (germplasm) {
            this.selectMatchesResult[this.dataRow[HEADERS.ROW_NUMBER]] = germplasm.gid;
            this.next();
        }

        this.matches = this.dataRow[HEADERS.GID_MATCHES];
        this.name = this.dataRow[HEADERS.DESIGNATION];
        this.rowNumber = this.dataRow[HEADERS.ROW_NUMBER];
    }

    next() {
        if (this.isFinish()) {
            this.modal.close(this.selectMatchesResult);
            return;
        }
        if (this.useSameMatchForAllOcurrences) {
            this.sameOccurrencesMap[this.dataRow[HEADERS.DESIGNATION]] = this.selectedGermplasm;
            this.useSameMatchForAllOcurrences = false;

        }
        this.processMatch(++this.matchNumber);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    isFinish() {
        return this.matchNumber === this.unassignedCount;
    }

    onSelectMatch(germplasm: GermplasmDto) {
        this.selectedGermplasm = germplasm;
        this.selectMatchesResult[this.dataRow[HEADERS.ROW_NUMBER]] = germplasm.gid;
        this.isIgnoreMatch = false;
    }

    ignoreMatch() {
        this.selectedGermplasm = null;
        this.selectMatchesResult[this.dataRow[HEADERS.ROW_NUMBER]] = null;
        this.next();
    }
}
