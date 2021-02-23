import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { Lot } from '../../shared/inventory/model/lot.model';
import { Transaction } from '../../shared/inventory/model/transaction.model';
import { InventoryUnit } from '../../shared/inventory/model/inventory-unit.model';
import { TransactionService } from '../../shared/inventory/service/transaction.service';
import { HttpErrorResponse } from '@angular/common/http';
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
    studyId;

    units: Promise<InventoryUnit[]>;
    storageLocations: Promise<Location[]>;
    favoriteLocations: Promise<Location[]>;

    favoriteLocation = false;
    storageLocationType = [1500];
    initialDepositRequired = false;
    storageLocIdSelected;
    favoriteLocIdSelected;

    isConfirmDeposit = false;
    isLoading = false;

    showHeader: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private transactionService: TransactionService,
                private inventoryService: InventoryService,
                private jhiAlertService: JhiAlertService,
                private lotService: LotService,
                private eventManager: JhiEventManager,
                private paramContext: ParamContext,
                private germplasmManagerContext: GermplasmManagerContext,
                private activeModal: NgbActiveModal
    ) {
        this.paramContext.readParams();
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.searchRequestId = queryParams.searchRequestId;
        this.showHeader = (queryParams.showHeader === 'true') ? true : false;

        if (queryParams.studyId) {
            // studyId has value if this Lot Creation page is called from Study Manager.
            // In this case, deposit is required.
            this.studyId = queryParams.studyId;
            this.initialDepositRequired = true;
        }

        this.lot = new Lot();
        this.deposit = new Transaction();

        this.units = this.inventoryService.queryUnits().toPromise();

        this.storageLocations = this.inventoryService.queryLocation({ locationTypes: this.storageLocationType, favoritesOnly: false }).toPromise();
        this.favoriteLocations = this.inventoryService.queryLocation({ locationTypes: this.storageLocationType, favoritesOnly: true }).toPromise();

        this.storageLocations.then((storageLocations) => {
            const defaultLocation = storageLocations.find((location) => location.defaultLocation);
            this.storageLocIdSelected = defaultLocation ? defaultLocation.id : storageLocations[0] && storageLocations[0].id;
        });

        this.favoriteLocations.then((favoriteLocations) => {
            const defaultFavoriteLocation = favoriteLocations.find((location) => location.defaultLocation);
            this.favoriteLocIdSelected = defaultFavoriteLocation ? defaultFavoriteLocation.id : favoriteLocations[0] && favoriteLocations[0].id;
        });
    }

    ngOnInit() {
    }

    clear() {
        // TODO
    }

    save() {
        this.isLoading = true;
        this.lot.locationId = this.favoriteLocation ? this.favoriteLocIdSelected : this.storageLocIdSelected;
        const lotGeneratorBatchRequest = {
            searchComposite: this.getSearchComposite(),
            lotGeneratorInput: Object.assign({
                generateStock: true,
                stockPrefix: this.model.stockIdPrefix
            }, this.lot),
            studyId: this.studyId
        };
        this.lotService.createLots(lotGeneratorBatchRequest)
            .subscribe(
            (res) => this.createDeposit(res),
            (res) => this.onError(res));
    }

    private getSearchComposite(): SearchComposite<any, number> {
        if (this.germplasmManagerContext.searchComposite) {
            return this.germplasmManagerContext.searchComposite;
        }
        return {
                itemIds: null,
                searchRequest: this.searchRequestId
            };
    }

    private createDeposit(lotUUIDs: string[]) {
        if (this.initialDepositRequired) {
            const lotDepositRequest = {
                selectedLots: <SearchComposite<any, string>>({ searchRequest: null, itemIds: lotUUIDs }),
                notes: this.deposit.notes,
                depositsPerUnit: {},
                sourceStudyId: this.studyId
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
        this.jhiAlertService.addAlert({ msg: 'lot-creation.success', type: 'success', toast: false, params: { param: lotUUIDs.length } }, null);
        this.isSuccess = true;
        this.isLoading = false;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
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
