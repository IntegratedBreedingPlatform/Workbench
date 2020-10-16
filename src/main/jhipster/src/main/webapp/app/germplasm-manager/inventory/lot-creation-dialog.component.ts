import { Component, OnInit, ViewEncapsulation } from '@angular/core';
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

@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-lot-creation-dialog',
    templateUrl: './lot-creation-dialog.component.html',
    // TODO migrate IBP-4093
    styleUrls: ['../../../content/css/global-bs3.css' ]
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

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private transactionService: TransactionService,
                private inventoryService: InventoryService,
                private jhiAlertService: JhiAlertService,
                private lotService: LotService,
                private eventManager: JhiEventManager,
                private paramContext: ParamContext
    ) {
        this.paramContext.readParams();
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.searchRequestId = queryParams.searchRequestId;

        if (queryParams.studyId) {
            // studyId has value if this Lot Creation page is called from Study Manager.
            // In this case, deposit is required.
            this.studyId = queryParams.studyId;
            this.initialDepositRequired = true;
        }

        this.lot = new Lot();
        this.deposit = new Transaction();

        this.units = this.inventoryService.queryUnits().toPromise();

        this.storageLocations = this.inventoryService.queryLocation({ locationTypes: this.storageLocationType, favoriteLocations: false }).toPromise();
        this.favoriteLocations = this.inventoryService.queryLocation({ locationTypes: this.storageLocationType, favoriteLocations: true }).toPromise();

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
        this.lot.locationId = this.favoriteLocation ? this.favoriteLocIdSelected : this.storageLocIdSelected;
        const lotGeneratorBatchRequest = {
            searchComposite: <SearchComposite>({
                itemIds: null,
                searchRequest: this.searchRequestId
            }),
            lotGeneratorInput: Object.assign({
                generateStock: true,
                stockPrefix: this.model.stockIdPrefix
            }, this.lot)
        };
        this.lotService.createLots(lotGeneratorBatchRequest).subscribe(
            (res) => this.createDeposit(res),
            (res) => this.onError(res));
    }

    private createDeposit(lotUUIDs: string[]) {
        if (this.initialDepositRequired) {
            const lotDepositRequest = {
                selectedLots: <SearchComposite>({ searchRequest: null, itemIds: lotUUIDs }),
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
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
        }
    }
}
