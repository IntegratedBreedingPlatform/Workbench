import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { Lot } from '../../shared/inventory/model/lot.model';
import { Transaction } from '../../shared/inventory/model/transaction.model';
import { InventoryUnit } from '../../shared/inventory/model/inventory-unit.model';
import { TransactionService } from '../../shared/inventory/service/transaction.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { LotService } from '../../shared/inventory/service/lot.service';
import { InventoryService } from '../../shared/inventory/service/inventory.service';
import { Location } from '../../shared/model/location.model';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { ParamContext } from '../../shared/service/param.context';
import { SearchComposite } from '../../shared/model/search-composite';
import { AlertService } from '../../shared/alert/alert.service';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmManagerContext } from '../germplasm-manager.context';
import { SearchOrigin, SearchOriginComposite } from '../../shared/model/Search-origin-composite';
import { LocationService } from '../../shared/location/service/location.service';

@Component({
    selector: 'jhi-lot-creation-dialog',
    templateUrl: './lot-creation-dialog.component.html'
})
export class LotCreationDialogComponent implements OnInit {

    STOCK_ID_PREFIX_REGEX = '(^\\w*[a-zA-Z]$|^$)';

    /** Indicates that the creation process has finished */
    isSuccess = false;

    lot: Lot;
    model = { stockIdPrefix: '' };
    deposit: Transaction;
    searchRequestId;
    searchOrigin: SearchOrigin;

    units: Promise<InventoryUnit[]>;
    storageLocations: Promise<Location[]>;
    favoriteLocations: Promise<Location[]>;

    initialDepositRequired = false;
    storageLocIdSelected;

    isConfirmDeposit = false;
    isLoading = false;

    openedFromWorkbench: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private transactionService: TransactionService,
                private inventoryService: InventoryService,
                private lotService: LotService,
                private eventManager: JhiEventManager,
                private paramContext: ParamContext,
                private germplasmManagerContext: GermplasmManagerContext,
                private activeModal: NgbActiveModal,
                private alertService: AlertService,
                private locationService: LocationService
    ) {
        this.paramContext.readParams();
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.searchRequestId = queryParams.searchRequestId;
        this.searchOrigin = queryParams.searchOrigin;
        this.openedFromWorkbench = (this.searchOrigin === SearchOrigin.GERMPLASM_SEARCH) ? true : false;

        if (this.searchOrigin === SearchOrigin.MANAGE_STUDY_SOURCE || this.searchOrigin === SearchOrigin.MANAGE_STUDY_PLOT) {
            // searchOrigin is MANAGE_STUDY_SOURCE or MANAGE_STUDY_PLOT indicate that Lot Creation page is called
            // from Study Manager and the deposit is required.
            this.initialDepositRequired = true;
        }

        this.lot = new Lot();
        this.deposit = new Transaction();

        this.units = this.inventoryService.queryUnits().toPromise();
    }

    ngOnInit() {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isLoading = true;
        this.lot.locationId = this.storageLocIdSelected;

        const lotGeneratorBatchRequest = {
            searchComposite: this.getSearchComposite(),
            lotGeneratorInput: Object.assign({
                generateStock: true,
                stockPrefix: this.model.stockIdPrefix
            }, this.lot)
        };
        this.lotService.createLots(lotGeneratorBatchRequest)
            .subscribe(
            (res) => this.createDeposit(res),
            (res) => this.onError(res));
    }

    private getSearchComposite(): SearchComposite<any, number> {
        if (this.searchOrigin === SearchOrigin.GERMPLASM_SEARCH) {
            return this.germplasmManagerContext.searchComposite;
        } else if (this.searchOrigin === SearchOrigin.MANAGE_STUDY_SOURCE || this.searchOrigin === SearchOrigin.MANAGE_STUDY_PLOT) {
            const searchTypeComposite = new SearchOriginComposite(this.searchRequestId, this.searchOrigin);
            return {
                searchRequest: searchTypeComposite
            };
        }
    }

    private createDeposit(lotUUIDs: string[]) {
        if (this.initialDepositRequired) {
            const lotDepositRequest = {
                selectedLots: <SearchComposite<any, string>>({ searchRequest: null, itemIds: lotUUIDs }),
                notes: this.deposit.notes,
                depositsPerUnit: {},
                searchComposite: this.getSearchComposite()
            };
            this.units.then((units) => {
                const lotUnit = units.filter((unit) => unit.id === this.lot.unitId.toString());
                lotDepositRequest.depositsPerUnit[lotUnit[0].name] = this.deposit.amount;
                if (this.isConfirmDeposit) {
                    this.transactionService.createConfirmedDeposits(lotDepositRequest).subscribe(
                        (res) => this.onSaveSuccess(lotUUIDs),
                        (res) => this.onError(res));
                } else {
                    this.transactionService.createPendingDeposits(lotDepositRequest).subscribe(
                        (res) => this.onSaveSuccess(lotUUIDs),
                        (res) => this.onError(res));
                }
            });
        } else {
            this.onSaveSuccess(lotUUIDs);
        }
    }

    private onSaveSuccess(lotUUIDs: string[]) {
        this.isSuccess = true;
        this.isLoading = false;
        this.alertService.success('lot-creation.success', { param: lotUUIDs.length }, null);
        this.eventManager.broadcast({ name: 'columnFiltersChanged', content: '' });
        this.activeModal.close();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
        this.isLoading = false;
    }

    close() {
        this.activeModal.dismiss();
    }

}

@Component({
    selector: 'jhi-lot-creation-popup',
    template: ''
})

export class LotCreationPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(private alertService: AlertService,
                private route: ActivatedRoute,
                private popupService: PopupService
    ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.popupService
                .open(LotCreationDialogComponent as Component);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
