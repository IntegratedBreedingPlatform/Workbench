import { Component, OnInit } from '@angular/core';
import { GermplasmListImportComponent, HEADERS } from './germplasm-list-import.component';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListImportContext } from './germplasm-list-import.context';
import { finalize } from 'rxjs/internal/operators/finalize';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { toUpper } from '../../shared/util/to-upper';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';

@Component({
    selector: 'jhi-germplasm-list-import-review',
    templateUrl: './germplasm-list-import-review.component.html'
})
export class GermplasmListImportReviewComponent implements OnInit {

    HEADERS = HEADERS;
    page = 0;
    pageSize = 10;

    isLoading: boolean;
    isSaving: boolean;

    skipMultipleMatches: boolean;
    skipDataWithoutMatches: boolean;

    // show matches html section
    showMatchesOption = SHOW_MATCHES_OPTIONS.ALL;
    SHOW_MATCHES_OPTIONS = SHOW_MATCHES_OPTIONS;

    // rows from file/data that matches
    dataSingleMatches: any[] = [];
    dataMultipleMatches: any[] = [];

    // rows from file/data that doesn't match
    dataWithOutMatches: any[] = [];

    // table displayed in html
    rows: any[] = [];

    // matches from db
    matches: GermplasmDto[];
    matchesByGUID: { [key: string]: GermplasmDto; } = {};
    matchesByGid: { [key: string]: GermplasmDto; } = {};
    matchesByName: { [key: string]: GermplasmDto[]; } = {};

    selectManualMatchesResult: any = {};
    selectMultipleMatchesResult: any = {};

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private popupService: PopupService,
        private alertService: AlertService,
        private germplasmService: GermplasmService,
        private germplasmManagerContext: GermplasmManagerContext,
        private router: Router,
        private eventManager: JhiEventManager,
        private context: GermplasmListImportContext
    ) {
    }

    ngOnInit(): void {
        this.isLoading = true;

        const guids = [];
        const gids = [];
        const names = [];

        this.context.data.forEach((row) => {
            if (row[HEADERS['GUID']]) {
                guids.push(row[HEADERS['GUID']]);
            }
            if (row[HEADERS['GID']]) {
                gids.push(row[HEADERS['GID']]);
            }

            if (row[HEADERS['DESIGNATION']]) {
                names.push(row[HEADERS['DESIGNATION']]);

            }
        });
        this.isLoading = true;
        this.germplasmService.getGermplasmMatches(undefined, guids, gids, names).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((matches) => {
            this.matches = matches;
            this.matchesByGUID = {};
            this.matchesByGid = {};
            this.matchesByName = {};

            this.matches.forEach((match) => {
                if (match.germplasmUUID) {
                    this.matchesByGUID[toUpper(match.germplasmUUID)] = match;
                }
                if (match.gid) {
                    this.matchesByGid[match.gid] = match;
                }
                if (match.names) {
                    match.names.forEach((name) => {
                        if (!this.matchesByName[toUpper(name.name)]) {
                            this.matchesByName[toUpper(name.name)] = [];
                        }
                        this.matchesByName[toUpper(name.name)].push(match);
                    });
                }

            });

            this.context.data.forEach((row) => {
                const guidMatch = this.matchesByGUID[toUpper(row[HEADERS.GUID])];
                const gidMatch = this.matchesByGid[row[HEADERS.GID]];
                const nameMatches = this.matchesByName[toUpper(row[HEADERS.DESIGNATION])];

                if (guidMatch) {
                    this.dataSingleMatches.push(row);
                    row[HEADERS['GID MATCHES']] = guidMatch.gid;
                } else if (gidMatch) {
                    this.dataSingleMatches.push(row);
                    row[HEADERS['GID MATCHES']] = gidMatch.gid;
                } else if (nameMatches) {
                    if (nameMatches.length > 1) {
                        this.dataMultipleMatches.push(row);
                        row[HEADERS['GID MATCHES']] = nameMatches.map((m) => m.gid).join(', ');

                    } else {
                        this.dataSingleMatches.push(row);
                        row[HEADERS['GID MATCHES']] = nameMatches[0].gid;
                    }
                } else {
                    this.dataWithOutMatches.push(row);
                }
            });
            this.rows = [...this.dataSingleMatches, ...this.dataMultipleMatches, ...this.dataWithOutMatches];
        });

    }

    dismiss() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component, { backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.import.cancel.confirm');
        confirmModalRef.result.then(() => this.modal.dismiss());
    }

    back() {
        this.modal.close();
        const modalRef = this.modalService.open(GermplasmListImportComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    keys(obj) {
        return Object.keys(obj);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    toUpper(param) {
        return toUpper(param);
    }

    onShowMatchesOptionChange() {
        switch (this.showMatchesOption) {
            case SHOW_MATCHES_OPTIONS.ALL:
                this.rows = [...this.dataSingleMatches, ...this.dataMultipleMatches, ...this.dataWithOutMatches];
                break;
            case SHOW_MATCHES_OPTIONS.WITH_A_SINGLE_MATCH:
                this.rows = this.dataSingleMatches;
                break;
            case SHOW_MATCHES_OPTIONS.WITH_MULTIPLE_MATCHES:
                this.rows = this.dataMultipleMatches;
                break;
            case SHOW_MATCHES_OPTIONS.WITHOUT_MATCHES:
                this.rows = this.dataWithOutMatches;
                break;
        }
    }

    async save() {

    }

    private onError(response: HttpErrorResponse | any) {
        if (!response.error) {
            this.alertService.error('error.general.client');
            console.error(response);
            return;
        }
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }
}

enum SHOW_MATCHES_OPTIONS {
    ALL = 'ALL',
    WITH_A_SINGLE_MATCH = 'WITH_A_SINGLE_MATCH',
    WITH_MULTIPLE_MATCHES = 'WITH_MULTIPLE_MATCHES',
    WITHOUT_MATCHES = 'WITHOUT_MATCHES'
}
