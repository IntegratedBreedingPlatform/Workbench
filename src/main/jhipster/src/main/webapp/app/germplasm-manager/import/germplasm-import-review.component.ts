import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';
import { GermplasmImportContext } from './germplasm-import.context';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmImportRequest } from '../../shared/germplasm/model/germplasm-import-request.model';
import { HEADERS } from './germplasm-import.component';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { SearchComposite } from '../../shared/model/search-composite';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';
import { GermplasmManagerContext } from '../germplasm-manager.context';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { LotService } from '../../shared/inventory/service/lot.service';
import { LotImportRequest, LotImportRequestLotList } from '../../shared/inventory/model/lot-import-request';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { GermplasmImportMatchesComponent } from './germplasm-import-matches.component';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

@Component({
    selector: 'jhi-germplasm-import-review',
    templateUrl: './germplasm-import-review.component.html'
})
export class GermplasmImportReviewComponent implements OnInit {

    dataBackupPrev = [];

    HEADERS = HEADERS;
    page = 0;
    pageSize = 10;

    isLoading: boolean;
    isSaving: boolean;

    // show matches html section
    showMatchesOption = SHOW_MATCHES_OPTIONS.ALL;
    SHOW_MATCHES_OPTIONS = SHOW_MATCHES_OPTIONS;

    // Review table html section

    // rows from file/data that matches
    dataMatches: any[] = [];
    // rows from file/data that doesn't match
    newRecords: any[] = [];
    // table displayed in html
    rows: any[] = [];

    // creation option html section
    CREATION_OPTIONS = CREATION_OPTIONS;
    creationOption = CREATION_OPTIONS.SELECT_EXISTING;
    isSelectMatchesAutomatically = true;

    // data loaded from server / utility maps

    // matches from db
    matches: GermplasmDto[];
    matchesByGUID: { [key: string]: GermplasmDto; } = {};
    matchesByName: { [key: string]: GermplasmDto; } = {};

    selectMatchesResult: any = {};

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private popupService: PopupService,
        private alertService: AlertService,
        private germplasmService: GermplasmService,
        private lotService: LotService,
        public context: GermplasmImportContext,
        private germplasmManagerContext: GermplasmManagerContext,
        private router: Router,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit(): void {
        this.dataBackupPrev = this.context.data.map((row) => Object.assign({}, row));

        const uuids = [];
        const names = {};
        this.context.data.forEach((row) => {
            if (row[HEADERS['GUID']]) {
                uuids.push(row[HEADERS['GUID']]);
            }
            this.context.nametypesCopy.forEach((nameType) => {
                if (row[nameType.code]) {
                    names[row[nameType.code]] = true;
                }
            })
        });
        this.isLoading = true;
        this.germplasmService.getGermplasmMatches(uuids, Object.keys(names)).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((matches) => {
            this.matches = matches;
            this.matchesByGUID = {};
            this.matchesByName = {};

            this.matches.forEach((match) => {
                this.matchesByGUID[match.germplasmUUID] = match;
                match.names.forEach((name) => this.matchesByName[name.name] = match);
            });
            this.context.data.forEach((row) => {
                const guidMatch = this.matchesByGUID[row[HEADERS.GUID]];
                const nameMatches = this.context.nametypesCopy.filter((nameType) => {
                    return Boolean(this.matchesByName[row[nameType.code]]);
                });
                if (guidMatch) {
                    this.dataMatches.push(row);
                    row[HEADERS['GID MATCHES']] = guidMatch.gid;
                } else if (nameMatches.length) {
                    this.dataMatches.push(row);
                    row[HEADERS['GID MATCHES']] = nameMatches
                        .map((nameType) => this.matchesByName[row[nameType.code]].gid)
                        // dedup
                        .filter((gid, i, array) => array.indexOf(gid) === i)
                        .join(',');
                } else {
                    this.newRecords.push(row);
                }
            });
            this.rows = [...this.dataMatches, ...this.newRecords];
        })
    }

    async save() {
        try {
            // process matches
            const unassignedMatches = this.dataMatches;

            if (unassignedMatches.length
                && this.creationOption === CREATION_OPTIONS.SELECT_EXISTING
                && !this.isSelectMatchesAutomatically) {

                const selectMatchesModalRef = this.modalService.open(GermplasmImportMatchesComponent as Component,
                    { size: 'lg', backdrop: 'static' });
                selectMatchesModalRef.componentInstance.unassignedMatches = unassignedMatches;
                selectMatchesModalRef.componentInstance.matchesByName = this.matchesByName;
                selectMatchesModalRef.componentInstance.matchesByGUID = this.matchesByGUID;
                try {
                    this.selectMatchesResult = await selectMatchesModalRef.result;
                } catch (rejected) {
                    return;
                }
            } else if (this.isSelectMatchesAutomatically) {
                // TODO
            } else if (this.creationOption === CREATION_OPTIONS.CREATE_NEW) {
                // TODO
            }

            const newEntries = this.context.data.filter((row) => {
                return !this.selectMatchesResult[row[HEADERS.ENTRY_NO]] && !this.matchesByGUID[row[HEADERS.GUID]];
            });

            // final confirmation

            const countMatchByGUID = this.context.data.filter((row) => this.matchesByGUID[row[HEADERS.GUID]]).length,
                countMatchByName = this.context.data.filter((row) => this.selectMatchesResult[row[HEADERS.ENTRY_NO]]).length,
                countNew = this.newRecords.length,
                countIgnored = newEntries.length - countMatchByName - countMatchByGUID;
            const messages = [];
            if (countNew) {
                messages.push(this.translateService.instant('germplasm.import.review.summary.new', { param: countNew }));
            }
            if (countIgnored) {
                messages.push(this.translateService.instant('germplasm.import.review.summary.ignored', { param: countIgnored }));
            }
            if (countMatchByName) {
                messages.push(this.translateService.instant('germplasm.import.review.summary.match.by.name', { param: countMatchByName }));
            }
            if (countMatchByGUID) {
                messages.push(this.translateService.instant('germplasm.import.review.summary.match.by.guid', { param: countMatchByGUID }));
            }
            const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component,
                { windowClass: 'modal-medium', backdrop: 'static' });
            confirmModalRef.componentInstance.message = this.translateService.instant('germplasm.import.review.summary.message', {
                param: messages.map((item) => '<li>' + item + '</li>').join('')
            });
            try {
                const confirmModalResult = await confirmModalRef.result;
            } catch (rejected) {
                return;
            }

            // Proceed with save

            this.isSaving = true;
            const resp: any = await this.germplasmService.importGermplasm(newEntries.map((row) => {
                return <GermplasmImportRequest>({
                    clientId: row[HEADERS.ENTRY_NO],
                    germplasmUUID: row[HEADERS.GUID],
                    locationAbbr: row[HEADERS['LOCATION ABBR']],
                    breedingMethodAbbr: row[HEADERS['BREEDING METHOD']],
                    reference: row[HEADERS['REFERENCE']],
                    preferredName: row[HEADERS['PREFERRED NAME']],
                    creationDate: row[HEADERS['CREATION DATE']],
                    names: this.context.nametypesCopy.reduce((map, name) => {
                        if (row[name.code]) {
                            map[name.code] = row[name.code];
                        }
                        return map;
                    }, {}),
                    attributes: this.context.attributesCopy.reduce((map, attribute) => {
                        if (row[attribute.code]) {
                            map[attribute.code] = row[attribute.code];
                        }
                        return map;
                    }, {})
                });
            })).toPromise();
            this.onSaveSuccess(resp);
        } catch (error) {
            this.onError(error);
        }
        this.isSaving = false;
    }

    private onSaveSuccess(res: { [key: string]: { status: string, gids: number[] } }) {
        this.alertService.success('germplasm-list-creation.success');
        const gids = Object.values(res).map((r) => r.gids[0]);
        this.eventManager.broadcast({ name: 'filterByGid', content: gids });

        const inventoryData = this.context.data.filter((row) => row[HEADERS['STOCK ID']]
            || row[HEADERS['STORAGE LOCATION ABBR']]
            || row[HEADERS['UNITS']]
            || row[HEADERS['AMOUNT']]);

        if (inventoryData.length) {
            // create inventory in the background
            // Option 1 TODO Pending?
            this.lotService.importLotsWithInitialBalance(
                <LotImportRequest>({
                    lotList: inventoryData.map((row) => <LotImportRequestLotList>({
                        gid: res[row[HEADERS.ENTRY_NO]].gids[0],
                        initialBalance: row[HEADERS.AMOUNT],
                        storageLocationAbbr: row[HEADERS['STORAGE LOCATION ABBR']],
                        unitName: row[HEADERS.UNITS],
                        stockId: row[HEADERS['STOCK ID']]
                    })),
                    stockIdPrefix: this.context.stockIdPrefix
                })
            ).subscribe(
                () => {
                    // TODO IBP-4293
                    // this.alertService.success('germplasm.import.inventory.success', { param: inventoryData.length })
                },
                (error) => this.onError(error)
            );
            // Option 2
            // importLotsWithInitialBalance + import pending/confirmed deposits
        }

        // TODO save entryNo
        // TODO use this.selectMatchesResult

        const searchComposite = new SearchComposite<GermplasmSearchRequest, number>();
        searchComposite.itemIds = gids;
        this.germplasmManagerContext.searchComposite = searchComposite;

        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-creation-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
        this.modal.close();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }

    dismiss() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component, { backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm.import.cancel.confirm');
        confirmModalRef.result.then(() => this.modal.dismiss());
    }

    back() {
        this.modal.close();
        this.context.data = this.context.dataBackup.pop();
        const modalRef = this.modalService.open(GermplasmImportInventoryComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    keys(obj) {
        return Object.keys(obj);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    onShowMatchesOptionChange() {
        switch (this.showMatchesOption) {
            case SHOW_MATCHES_OPTIONS.ALL:
                this.rows = [...this.dataMatches, ...this.newRecords];
                break;
            case SHOW_MATCHES_OPTIONS.ONLY_MATCHES:
                this.rows = this.dataMatches;
                break;
            case SHOW_MATCHES_OPTIONS.NEW_RECORDS:
                this.rows = this.newRecords;
                break;
        }
    }
}

enum SHOW_MATCHES_OPTIONS {
    ALL = 'ALL',
    ONLY_MATCHES = 'ONLY_MATCHES',
    NEW_RECORDS = 'NEW_RECORDS'
}

enum CREATION_OPTIONS {
    SELECT_EXISTING = 'SELECT_EXISTING',
    CREATE_NEW = 'CREATE_NEW'
}
