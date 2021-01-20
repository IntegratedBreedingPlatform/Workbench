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

    matches: GermplasmDto[];
    showMatchesOption = SHOW_MATCHES_OPTIONS.ALL;
    SHOW_MATCHES_OPTIONS = SHOW_MATCHES_OPTIONS;
    dataMatches: any[] = [];
    newRecords: any[] = [];
    rows: any[] = [];

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
            const guidMatch = {}
                , nameMatch = {};
            this.matches.forEach((match) => {
                guidMatch[match.germplasmUUID] = true;
                match.names.forEach((name) => nameMatch[name.name] = true);
            });
            this.context.data.forEach((row) => {
                if (guidMatch[row[HEADERS.GUID]] || this.context.nametypesCopy.some((nameType) => {
                    return nameMatch[row[nameType.code]];
                })) {
                    this.dataMatches.push(row);
                } else {
                    this.newRecords.push(row);
                }
            });
            this.rows = [...this.dataMatches, ...this.newRecords];
        })
    }

    save() {
        this.isSaving = true;
        this.germplasmService.importGermplasm(this.context.data.map((row) => {
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
        })).pipe(finalize(() => {
            this.isSaving = false;
        })).subscribe(
            (res: any) => this.onSaveSuccess(res),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSaveSuccess(res: {[key: string]: {status: string, gids: number[]}}) {
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
                () =>  {
                    // TODO IBP-4293
                    // this.alertService.success('germplasm.import.inventory.success', { param: inventoryData.length })
                },
                (error) => this.onError(error)
            );
            // Option 2
            // importLotsWithInitialBalance + import pending/confirmed deposits
        }


        // TODO save entryNo

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
        this.modal.dismiss();
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

    onOptionChange() {
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
