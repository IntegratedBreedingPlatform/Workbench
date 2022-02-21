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
    useSameMatchForAllOccurrences: boolean;
    selectedGermplasm: GermplasmDto;
    sameOccurrencesMap: { [key: string]: { entry: number, germplasmId: number } } = {}; // Key preferred Name.

    constructor(
        private modal: NgbActiveModal
    ) {
    }

    ngOnInit(): void {
        this.useSameMatchForAllOccurrences = false;
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
            --this.matchNumber;
            this.dataRow = this.unassignedMatches[this.matchNumber - 1];
            this.rowNumber = this.dataRow[HEADERS.ROW_NUMBER];

            const sameOccurrence = this.sameOccurrencesMap[this.dataRow[HEADERS.DESIGNATION]];
            this.useSameMatchForAllOccurrences = false;
            if (sameOccurrence && sameOccurrence.entry !== this.rowNumber) {
                return this.back();
            } else if (sameOccurrence && sameOccurrence.entry === this.rowNumber) {
                this.useSameMatchForAllOccurrences = true;
            }

            this.page = 0;
            this.isIgnoreMatch = false;
            this.matches = this.dataRow[HEADERS.GID_MATCHES];
            this.name = this.dataRow[HEADERS.DESIGNATION];
        }
    }

    private processMatch(matchNumber) {
        this.dataRow = this.unassignedMatches[matchNumber - 1];
        this.rowNumber = this.dataRow[HEADERS.ROW_NUMBER];

        const sameOccurrence = this.sameOccurrencesMap[this.dataRow[HEADERS.DESIGNATION]];
        if (sameOccurrence && sameOccurrence.entry !== this.rowNumber) {
            this.selectMatchesResult[this.dataRow[HEADERS.ROW_NUMBER]] = sameOccurrence.germplasmId;
            this.next();
        } else if (sameOccurrence && sameOccurrence.entry === this.rowNumber) {
            this.useSameMatchForAllOccurrences = true;
        }
        this.page = 0;
        this.isIgnoreMatch = false;
        this.matches = this.dataRow[HEADERS.GID_MATCHES];
        this.name = this.dataRow[HEADERS.DESIGNATION];
    }

    next() {
        if (this.isFinish()) {
            this.modal.close(this.selectMatchesResult);
            return;
        }
        if (this.useSameMatchForAllOccurrences) {
            this.sameOccurrencesMap[this.dataRow[HEADERS.DESIGNATION]] = { entry: this.dataRow[HEADERS.ROW_NUMBER], germplasmId: this.selectedGermplasm.gid };
            this.useSameMatchForAllOccurrences = false;
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

    checkUseSameMatchForAllOcurrences() {
        if (!this.useSameMatchForAllOccurrences) {
            this.sameOccurrencesMap[this.dataRow[HEADERS.DESIGNATION]] = null;
        } else if (this.selectedGermplasm) {
            this.sameOccurrencesMap[this.dataRow[HEADERS.DESIGNATION]] = { entry: this.dataRow[HEADERS.ROW_NUMBER], germplasmId: this.selectedGermplasm.gid };
        }
    }
}
