import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { Lot } from '../../shared/inventory/model/lot.model';
import { Transaction } from '../../shared/inventory/model/transaction.model';
import { InventoryUnit } from '../../shared/inventory/model/inventory-unit.model';
import { MANAGE_LOT_PERMISSIONS } from '../../shared/auth/permissions';
import { TransactionService } from '../../shared/inventory/service/transaction.service';
import { HttpErrorResponse } from '@angular/common/http';
import { HttpResponse } from '@angular/common/http';
import { LotService } from '../../shared/inventory/service/lot.service';
import { InventoryService } from '../../shared/inventory/service/inventory.service';
import { Location } from '../../shared/model/location.model';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { ParamContext } from '../../shared/service/param.context';

@Component({
    selector: 'jhi-lot-creation-dialog',
    templateUrl: './lot-creation-dialog.component.html'
})
export class LotCreationDialogComponent implements OnInit {

    STOCK_ID_PREFIX_REGEX = '(^\\w*[a-zA-Z]$|^$)';
    CREATE_DEPOSIT_INVENTORY_PERMISSION = [...MANAGE_LOT_PERMISSIONS, 'DEPOSIT_INVENTORY', 'CREATE_CONFIRMED_DEPOSITS'];

    lot: Lot;
    model;
    deposit: Transaction;

    units: Promise<InventoryUnit[]>;
    storageLocations: Promise<Location[]>;
    favoriteLocations: Promise<Location[]>;

    favoriteLocation = false;
    storageLocationType = [1500];
    initialDeposit = false;
    storageLocIdSelected;
    favoriteLocIdSelected;

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

        this.model = { stockIdPrefix: '' };
        this.lot = new Lot();
        this.deposit = new Transaction();
        this.deposit.transactionType = 'Deposit';
        this.deposit.transactionStatus = 'Confirmed';

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

        // this.lot.unitId = null;

    }

    ngOnInit() {
    }

    clear() {
        // TODO
    }

    save() {
        this.lot.locationId = this.favoriteLocation ? this.favoriteLocIdSelected : this.storageLocIdSelected;
        this.lotService.createLots(this.lot, this.model.stockIdPrefix).subscribe(
            (res) => this.createDeposit(res), (error) => this.onError(error));
    }

    private createDeposit(lotUUID: any) {
        this.lot.lotUUID = lotUUID;
        if (this.initialDeposit) {
            const lotDepositRequest = {
                selectedLots: { searchRequest: null, itemIds: [lotUUID] },
                notes: this.deposit.notes,
                depositsPerUnit: {}
            };
            this.units.then((units) => {
                const lotUnit = units.filter((unit) => unit.id === this.lot.unitId.toString());
                lotDepositRequest.depositsPerUnit[lotUnit[0].name] = this.deposit.amount;
                this.transactionService.createConfirmedDeposits(lotDepositRequest).subscribe(
                        (res: HttpResponse<Lot>) => this.onSaveSuccess(res),
                        (res: HttpErrorResponse) => this.onError(res.error.errors[0]));
            });
        } else {
            this.onSaveSuccess(lotUUID);
        }

    }

    private onSaveSuccess(result: any) {
        // TODO toast false?
        this.jhiAlertService.success('inventoryManagerApp.lot.createLot.success', null, null);
    }

    private onError(response: HttpErrorResponse) {
        this.jhiAlertService.error('error.custom', { param: formatErrorList(response.error.errors) });
    }
}
