import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HEADERS } from './germplasm-import.component';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';
import { GermplasmImportReviewComponent } from './germplasm-import-review.component';
import { GermplasmImportContext } from './germplasm-import.context';
import { LocationService } from '../../shared/location/service/location.service';
import { InventoryService } from '../../shared/inventory/service/inventory.service';
import { LocationTypeEnum } from '../../shared/location/model/location.model';
import { Location } from '../../shared/location/model/location';
import { InventoryUnit } from '../../shared/inventory/model/inventory-unit.model';

@Component({
    selector: 'jhi-germplasm-import-inventory',
    templateUrl: './germplasm-import-inventory.component.html'
})
export class GermplasmImportInventoryComponent implements OnInit {

    dataBackupPrev = [];

    STOCK_ID_PREFIX_REGEX = '(^\\w*[a-zA-Z]$|^$)';

    createInventoryLots = true;
    stock_IdPrefix: string;

    seedStorageLocations: Promise<Location[]>;
    favoriteSeedStorageLocations: Promise<Location[]>;
    locationSelected: string;
    useFavoriteLocations = true;

    units: Promise<InventoryUnit[]>
    unitSelected: string;

    deposit: any;
    amountConfirmed = false;

    completeInventoryData = true;

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private popupService: PopupService,
        public context: GermplasmImportContext,
        private locationService: LocationService,
        private inventoryService: InventoryService,
    ) {
    }

    ngOnInit(): void {
        this.dataBackupPrev = this.context.data.map((row) => Object.assign({}, row));
        this.loadLocations();
        this.loadUnits();
        this.deposit = { amount: null };
        this.createInventoryLots = this.haveSomeInventoryDetails();
    }

    next() {
        this.modal.close();
        this.context.dataBackup.push(this.dataBackupPrev);
        const modalRef = this.modalService.open(GermplasmImportReviewComponent as Component,
            { windowClass: 'modal-autofit', backdrop: 'static' });
    }

    dismiss() {
        this.modal.dismiss();
    }

    back() {
        this.modal.close();
        this.context.data = this.context.dataBackup.pop();
        const modalRef = this.modalService.open(GermplasmImportBasicDetailsComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    // TODO perform complete all entries or Complete entries with some data. Omit entries if check create Inventory is disable.
    canProceed(f) {
        const form = f.form;
        return !this.createInventoryLots
            ||  (form.valid
                && (this.stock_IdPrefix || this.hasAllStockIds())
                && (this.locationSelected || this.hasAllLocations())
                && (this.locationSelected || this.hasAllUnits())
                && (this.deposit.amount || this.hasAllAmounts())
                )
    }

    hasAllInventoryDetails() {
        return this.hasAllStockIds() && this.hasAllLocations() && this.hasAllUnits() && this.hasAllAmounts();
    }

    enableOptionsToComplete() {
        return !this.hasAllInventoryDetails() && this.haveSomeInventoryDetails();
    }

    haveSomeInventoryDetails() {
        return this.context.data.some((row) => {
            return row[HEADERS['STOCK ID']]
                || row[HEADERS['STORAGE LOCATION ABBR']]
                || row[HEADERS['UNITS']]
                || row[HEADERS['AMOUNT']]
        });
    }

    loadLocations() {
        this.seedStorageLocations = this.locationService.queryLocationsByType([LocationTypeEnum.SEED_STORAGE_LOCATION], false).toPromise();
        this.favoriteSeedStorageLocations = this.locationService.queryLocationsByType([LocationTypeEnum.SEED_STORAGE_LOCATION], true).toPromise();
    }

    loadUnits() {
        this.units = this.inventoryService.queryUnits().toPromise();
    }

    hasAllStockIds() {
        return this.context.data.every((row) => row[HEADERS['STOCK ID']]);
    }

    hasAllLocations() {
        return this.context.data.every((row) => row[HEADERS['STORAGE LOCATION ABBR']]);
    }

    hasAllUnits() {
        return this.context.data.every((row) => row[HEADERS['UNITS']]);
    }

    hasAllAmounts() {
        return this.context.data.every((row) => row[HEADERS['AMOUNT']]);

    }
}
