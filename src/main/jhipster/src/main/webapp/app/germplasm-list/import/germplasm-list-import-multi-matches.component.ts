import { Component, OnInit } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListImportContext } from './germplasm-list-import.context';
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

    matchesByName: { [key: string]: GermplasmDto[]; } = {};
    selectMatchesResult: { [key: string]: number };
    // internal usage
    matchNumber = 1;
    unassignedCount: number;
    matches: GermplasmDto[] = [];
    dataRow: any = {};

    // modal input
    unassignedMatches: any[] = [];

    name: string;

    isIgnoreMatch: boolean;

    constructor(
        private context: GermplasmListImportContext,
        private modal: NgbActiveModal
    ) {
    }

    ngOnInit(): void {
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
        this.matches = this.matchesByName[this.dataRow[HEADERS.DESIGNATION]];
        this.name = this.dataRow[HEADERS.DESIGNATION];
    }

    next() {
        if (this.isFinish()) {
            this.modal.close(this.selectMatchesResult);
            return;
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
        this.selectMatchesResult[this.dataRow['ENTRY_NO']] = germplasm.gid;
        this.isIgnoreMatch = false;
    }

    ignoreMatch() {
        this.selectMatchesResult[this.dataRow['ENTRY_NO']] = null;
        this.next();
    }
}
