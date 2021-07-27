import { Component, OnInit } from '@angular/core';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmImportContext } from './germplasm-import.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HEADERS } from './germplasm-import.component';
import { animate, style, transition, trigger } from '@angular/animations';
import { toUpper } from '../../shared/util/to-upper';

@Component({
    selector: 'jhi-germplasm-import-matches',
    templateUrl: 'germplasm-import-matches.component.html',
    animations: [
        trigger('tableAnimation', [
            transition('void => *', [
                style({ opacity: 0 }),
                animate(500)
            ]),
        ])
    ]
})
export class GermplasmImportMatchesComponent implements OnInit {

    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;

    // modal input
    unassignedMatches: any[] = [];
    matchesByName: { [key: string]: GermplasmDto[] };
    matchesByPUI: { [key: string]: GermplasmDto };
    // ENTRY_NO -> gid or null (new)
    selectMatchesResult: { [key: string]: number };

    // internal usage
    matchNumber = 1;
    unassignedCount: number;
    matches: GermplasmDto[] = [];
    dataRow: any = {};
    entryNo: string;
    name: string;
    puiMatch: GermplasmDto = null;
    isIgnoreAllRemaining: boolean;
    isIgnoreMatch: boolean;

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
        this.page = 0;
        this.isIgnoreMatch = false;

        this.dataRow = this.unassignedMatches[matchNumber - 1];
        this.puiMatch = this.matchesByPUI[toUpper(this.dataRow[HEADERS.PUI])];
        const gidMap = {};
        this.matches = this.context.nametypesCopy
            .filter((nameType) => Boolean(this.matchesByName[toUpper(this.dataRow[nameType.code])]))
            .reduce((array, nameType) => array.concat(this.matchesByName[toUpper(this.dataRow[nameType.code])]), [])
            // dedup
            .filter((germplasm: GermplasmDto) => {
                if (!gidMap[germplasm.gid]) {
                    gidMap[germplasm.gid] = true;
                    return true;
                }
                return false;
            });
        if (this.puiMatch && !gidMap[this.puiMatch.gid]) {
            this.matches.unshift(this.puiMatch);
        }
        this.entryNo = this.dataRow[HEADERS.ENTRY_NO];
    }

    onSelectMatch(germplasm: GermplasmDto) {
        this.selectMatchesResult[this.dataRow[HEADERS.ENTRY_NO]] = germplasm.gid;
        this.isIgnoreMatch = false;
        this.isIgnoreAllRemaining = false;
    }

    onIgnoreMatch() {
        if (this.isIgnoreMatch) {
            this.selectMatchesResult[this.dataRow[HEADERS.ENTRY_NO]] = null;
        }
    }

    onIgnoreAllRemaining() {
        if (this.isIgnoreAllRemaining) {
            // we consider also this match as "remaining"
            this.isIgnoreMatch = true;
            this.selectMatchesResult[this.dataRow[HEADERS.ENTRY_NO]] = null;
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
            this.modal.close(this.selectMatchesResult);
            return;
        }
        this.processMatch(++this.matchNumber);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    isFinish() {
        return this.matchNumber === this.unassignedCount
            || this.isIgnoreAllRemaining;
    }
}
