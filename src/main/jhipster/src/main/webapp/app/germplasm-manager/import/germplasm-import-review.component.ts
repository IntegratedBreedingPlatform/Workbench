import { Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';
import { GermplasmImportContext } from './germplasm-import.context';
import { GermplasmService, ImportGermplasmResultType } from '../../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmImportPayload, GermplasmImportRequest } from '../../shared/germplasm/model/germplasm-import-request.model';
import { HEADERS } from './germplasm-import.component';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmManagerContext } from '../germplasm-manager.context';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { LotService } from '../../shared/inventory/service/lot.service';
import { LotImportRequest, LotImportRequestLotList } from '../../shared/inventory/model/lot-import-request';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { GermplasmImportMatchesComponent } from './germplasm-import-matches.component';
import { GermplasmListEntry } from '../../shared/model/germplasm-list';
import { toUpper } from '../../shared/util/to-upper';
import { NameType } from '../../shared/germplasm/model/name-type.model';
import { GermplasmListCreationComponent } from '../../shared/list-creation/germplasm-list-creation.component';
import { listPreview } from '../../shared/util/list-preview';

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
    isFullAutomaticMatchNotPossible: boolean;

    // data loaded from server / utility maps

    // matches from db
    matches: GermplasmDto[];
    matchesByPUI: { [key: string]: GermplasmDto; } = {};
    matchesByName: { [key: string]: GermplasmDto[]; } = {};

    selectMatchesResult: any = {};
    inventoryData: any;
    importResult: ImportGermplasmResultType = {};

    // name columns with duplicates
    columnNamesWithDupes = {};

    listPreview = listPreview;

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

        const puis = [];
        const names = {};
        this.context.data.forEach((row) => {
            if (row[HEADERS['PUI']]) {
                puis.push(row[HEADERS['PUI']]);
            }
            this.context.nametypesCopy.forEach((nameType) => {
                if (row[nameType.code]) {
                    names[row[nameType.code]] = true;
                }
            });
        });
        this.isLoading = true;
        this.germplasmService.getGermplasmMatches(puis, Object.keys(names)).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((matches) => {
            this.matches = matches;
            this.matchesByPUI = {};
            this.matchesByName = {};

            this.matches.forEach((match) => {
                if (match.germplasmPUI) {
                    this.matchesByPUI[toUpper(match.germplasmPUI)] = match;
                }
                match.names.forEach((name) => {
                    if (!this.matchesByName[toUpper(name.name)]) {
                        this.matchesByName[toUpper(name.name)] = [];
                    }
                    this.matchesByName[toUpper(name.name)].push(match);
                });
            });

            this.context.data.forEach((row) => {
                const puiMatch = this.matchesByPUI[toUpper(row[HEADERS.PUI])];
                const nameMatches = this.context.nametypesCopy.filter((nameType) => {
                    return Boolean(this.matchesByName[toUpper(row[nameType.code])]);
                });
                if (puiMatch) {
                    this.dataMatches.push(row);
                    row[HEADERS['GID MATCHES']] = puiMatch.gid;
                } else if (nameMatches.length) {
                    this.dataMatches.push(row);
                    row[HEADERS['GID MATCHES']] = nameMatches
                        .reduce((array, nameType) => array.concat(this.matchesByName[toUpper(row[nameType.code])].map((m) => m.gid)), [])
                        // dedup
                        .filter((gid, i, array) => array.indexOf(gid) === i)
                        .join(', ');

                    const matchesByPrefName = this.matchesByName[toUpper(row[row[HEADERS['PREFERRED NAME']]])];
                    if (matchesByPrefName && matchesByPrefName.length === 1) {
                        // noop - full auto-match still posible
                    } else if (matchesByPrefName && matchesByPrefName.length > 1) {
                        // more than one match by pref name
                        this.isFullAutomaticMatchNotPossible = true;
                    } else if (nameMatches.length) {
                        /*
                         * Preferred name is new (no match), but other names have matches.
                         * Even if it's only one match, the system will show the manual
                         * selection process anyway, so that's it is clear we are not
                         * using the preferred name.
                         */
                        this.isFullAutomaticMatchNotPossible = true;
                    }
                } else {
                    this.newRecords.push(row);
                }
            });
            this.rows = [...this.dataMatches, ...this.newRecords];
            this.columnNamesWithDupes = this.findDupesInNewRecords(this.newRecords);
        });

        this.inventoryData = this.context.data.filter((row) => row[HEADERS['STOCK ID']]
            || row[HEADERS['STORAGE LOCATION ABBR']]
            || row[HEADERS['UNITS']]
            || row[HEADERS['AMOUNT']]);
    }

    private findDupesInNewRecords(newEntries: any[]) {
        const columnNamesWithDupes = {};
        // check every name column for dupes
        this.context.nametypesCopy.forEach((nameType) => {
            const namesSeen = {};
            newEntries.forEach((row) => {
                if (namesSeen[row[nameType.code]]) {
                    columnNamesWithDupes[nameType.code] = true;
                }
                if (row[nameType.code]) {
                    namesSeen[row[nameType.code]] = true;
                }
            });
        });

        /*
         * check dupes across preferred names:
         * ENTRY_NO LNAME DRVNM
         * 1        NAME1
         * 2              NAME1
         */
        {
            const preferredNameNote = this.translateService.instant('germplasm.import.review.new.records.dupes.preferred.name.note');
            const namesSeen = {};
            newEntries.forEach((row) => {
                /*
                 * If the column used for preferred name does not already contains duplicates inside the same column
                 * but it contains duplicates across different columns used as preferred name
                 */
                if (!columnNamesWithDupes[row[HEADERS['PREFERRED NAME']]]
                    && namesSeen[row[row[HEADERS['PREFERRED NAME']]]]) {
                    columnNamesWithDupes[HEADERS['PREFERRED NAME'] + preferredNameNote] = true;
                }
                namesSeen[row[row[HEADERS['PREFERRED NAME']]]] = true;
            });
        }
        return columnNamesWithDupes;
    }

    /**
     * Check duplicates again in case the user skips some of the matches and decide to add new records from there
     */
    private async checkDupesInNewRecords(newEntries: any[]) {
        /*
         * If we are already showing the warning for new records, no need to add another confirmation step.
         * The user is already aware that duplicates are going to be created.
         */
        if (this.size(this.columnNamesWithDupes)) {
            return true;
        }
        const columnNamesWithDupes = this.findDupesInNewRecords(newEntries);
        if (!this.size(columnNamesWithDupes)) {
            return true;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component,
            { windowClass: 'modal-medium', backdrop: 'static' });
        confirmModalRef.componentInstance.message = '<span style="word-break: break-word">' + this.translateService.instant('germplasm.import.review.new.records.dupes', {
            columns: listPreview(this.keys(columnNamesWithDupes))
        }) + '</span>';
        confirmModalRef.componentInstance.confirmLabel = this.translateService.instant('continue');
        try {
            await confirmModalRef.result;
        } catch (rejected) {
            return false;
        }
        return true;
    }

    async save() {
        try {
            let doContinue = await this.processMatches();
            if (!doContinue) {
                return;
            }

            const newEntries = this.context.data.filter((row) => {
                return !this.selectMatchesResult[row[HEADERS.ENTRY_NO]] && !this.matchesByPUI[toUpper(row[HEADERS.PUI])];
            });

            doContinue = await this.checkDupesInNewRecords(newEntries);
            if (!doContinue) {
                return;
            }

            doContinue = await this.showSummaryConfirmation();
            if (!doContinue) {
                return;
            }

            // Proceed with save

            if (newEntries.length) {
                this.isSaving = true;
                this.importResult = await this.germplasmService.importGermplasm(<GermplasmImportRequest>({
                    germplasmList: newEntries.map((row) => <GermplasmImportPayload>({
                        clientId: row[HEADERS.ENTRY_NO],
                        germplasmPUI: row[HEADERS.PUI],
                        locationAbbr: row[HEADERS['LOCATION ABBR']],
                        breedingMethodAbbr: row[HEADERS['BREEDING METHOD']],
                        reference: row[HEADERS['REFERENCE']],
                        preferredName: row[HEADERS['PREFERRED NAME']],
                        creationDate: row[HEADERS['CREATION DATE']],
                        progenitor1: row[HEADERS['PROGENITOR 1']],
                        progenitor2: row[HEADERS['PROGENITOR 2']],
                        names: this.context.nametypesCopy.reduce((map, name) => {
                            if (row[toUpper(name.code)]) {
                                map[name.code] = row[toUpper(name.code)];
                            }
                            return map;
                        }, {}),
                        attributes: this.context.attributesCopy.reduce((map, attribute) => {
                            if (row[toUpper(attribute.name)]) {
                                map[attribute.name] = row[toUpper(attribute.name)];
                            } else if (row[toUpper(attribute.alias)]) {
                                map[attribute.alias] = row[toUpper(attribute.alias)];
                            }
                            return map;
                        }, {})
                    })),
                    connectUsing: this.context.pedigreeConnectionType
                })).toPromise();
            }
            try {
                await this.saveInventory();
            } catch (inventoryError) {
                // save germplasm and inventory are two different services
                // show error and continue
                this.onError(inventoryError);
            }

            this.onSaveSuccess();
        } catch (error) {
            this.onError(error);
        }
        this.isSaving = false;
    }

    private async processMatches(): Promise<boolean> {
        const unassignedMatches = this.dataMatches;
        this.selectMatchesResult = {};

        if (unassignedMatches.length) {
            if (this.creationOption === CREATION_OPTIONS.SELECT_EXISTING) {
                if (this.isSelectMatchesAutomatically) {
                    unassignedMatches.forEach((row) => {
                        const puiMatch = this.matchesByPUI[toUpper(row[HEADERS.PUI])];
                        if (!puiMatch) {
                            const matches = this.matchesByName[toUpper(row[row[HEADERS['PREFERRED NAME']]])];
                            if (matches && matches.length === 1) {
                                this.selectMatchesResult[row[HEADERS.ENTRY_NO]] = matches[0].gid;
                            }
                        }
                    });
                }
                /*
                 * if 1) auto-matching didn't work:
                 *      a) multiple gids for preferred name or..
                 *      b) preferred name does not exist, but other names have matches
                 *    2) auto-matching unchecked
                 * -> open manual
                 */
                if (unassignedMatches.some((row) => !this.selectMatchesResult[row[HEADERS.ENTRY_NO]]
                    && !this.matchesByPUI[toUpper(row[HEADERS.PUI])])) {

                    const selectMatchesModalRef = this.modalService.open(GermplasmImportMatchesComponent as Component,
                        { size: 'lg', backdrop: 'static' });
                    selectMatchesModalRef.componentInstance.unassignedMatches = unassignedMatches;
                    selectMatchesModalRef.componentInstance.matchesByName = this.matchesByName;
                    selectMatchesModalRef.componentInstance.matchesByPUI = this.matchesByPUI;
                    selectMatchesModalRef.componentInstance.selectMatchesResult = this.selectMatchesResult;
                    try {
                        await selectMatchesModalRef.result;
                    } catch (rejected) {
                        return false;
                    }
                }
            } else if (this.creationOption === CREATION_OPTIONS.CREATE_NEW) {
                this.selectMatchesResult = {};
            }
        }

        return true;
    }

    private async showSummaryConfirmation() {
        const countMatchByPUI = this.context.data.filter((row) => this.matchesByPUI[toUpper(row[HEADERS.PUI])]).length,
            countMatchByName = this.context.data.filter((row) => this.selectMatchesResult[row[HEADERS.ENTRY_NO]]).length,
            countNew = this.newRecords.length,
            countIgnored = this.dataMatches.length - countMatchByName - countMatchByPUI;
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
        if (countMatchByPUI) {
            messages.push(this.translateService.instant('germplasm.import.review.summary.match.by.pui', { param: countMatchByPUI }));
        }
        if (this.inventoryData.length) {
            messages.push(this.translateService.instant('germplasm.import.review.summary.inventory', { param: this.inventoryData.length }));
        }
        // TODO ...and is new record
        // const hasProgenitorsCount = this.context.data.filter((row) => row[HEADERS['PROGENITOR 1'] || row[HEADERS['PROGENITOR 2']]]).length;
        // if (hasProgenitorsCount) {
        //     messages.push(this.translateService.instant('germplasm.import.review.summary.progenitors', { param: hasProgenitorsCount }));
        // }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component,
            { windowClass: 'modal-medium', backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm.import.review.summary.message', {
            param: messages.map((item) => '<li>' + item + '</li>').join('')
        });
        try {
            await confirmModalRef.result;
        } catch (rejected) {
            return false;
        }
        return true;
    }

    private onSaveSuccess() {
        const gids = Object.values(this.importResult).map((r) => r.gids[0]);
        if (gids.length) {
            this.alertService.success('germplasm.import.success');
        }
        gids.push(...this.context.data.filter((row) => this.selectMatchesResult[row[HEADERS.ENTRY_NO]])
            .map((row) => this.selectMatchesResult[row[HEADERS.ENTRY_NO]]));
        gids.push(...this.context.data.filter((row) => this.matchesByPUI[toUpper(row[HEADERS.PUI])])
            .map((row) => this.matchesByPUI[toUpper(row[HEADERS.PUI])].gid));

        this.eventManager.broadcast({ name: 'filterByGid', content: gids });

        this.modal.close();
        const germplasmListCreationModalRef = this.modalService.open(GermplasmListCreationComponent as Component,
            { size: 'lg', backdrop: 'static' });
        germplasmListCreationModalRef.componentInstance.entries = this.context.data.map((row) => {
            const entry = new GermplasmListEntry();
            entry.gid = this.getSavedGid(row);
            entry.entryCode = row[HEADERS.ENTRY_CODE];
            entry.entryNo = Number(row[HEADERS.ENTRY_NO]);
            return entry
        });
    }

    private async saveInventory() {
        if (this.inventoryData.length) {
            this.isSaving = true;
            return this.lotService.importLotsWithInitialBalance(
                <LotImportRequest>({
                    lotList: this.inventoryData.map((row) => {
                        return <LotImportRequestLotList>({
                            gid: this.getSavedGid(row),
                            initialBalance: row[HEADERS.AMOUNT],
                            storageLocationAbbr: row[HEADERS['STORAGE LOCATION ABBR']],
                            unitName: row[HEADERS.UNITS],
                            stockId: row[HEADERS['STOCK ID']],
                            pendingStatus: !this.context.amountConfirmed
                        });
                    }),
                    stockIdPrefix: this.context.stockIdPrefix
                })
            ).toPromise().then(
                () => {
                    // TODO IBP-4293
                    // this.alertService.success('germplasm.import.inventory.success', { param: this.inventoryData.length })
                }
            );
        }
        return true;
    }

    private getSavedGid(row) {
        if (this.importResult[row[HEADERS.ENTRY_NO]]) {
            return this.importResult[row[HEADERS.ENTRY_NO]].gids[0];
        } else if (this.matchesByPUI[toUpper(row[HEADERS.PUI])]) {
            return this.matchesByPUI[toUpper(row[HEADERS.PUI])].gid;
        } else {
            return this.selectMatchesResult[row[HEADERS.ENTRY_NO]];
        }
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

    toUpper(param) {
        return toUpper(param);
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

@Pipe({
    name: 'NameColumnPipe'
})
export class NameColumnPipePipe implements PipeTransform {
    transform(items: any[]): any {
        // exclude common name headers
        return items.filter((item: NameType) =>
            [HEADERS.LNAME.toString(), HEADERS.DRVNM.toString(), HEADERS.PUI.toString()].indexOf(item.code) === -1);
    }

}
