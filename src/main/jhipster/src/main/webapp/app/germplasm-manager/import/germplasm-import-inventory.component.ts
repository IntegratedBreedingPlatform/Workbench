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
import { InventoryUnit } from '../../shared/inventory/model/inventory-unit.model';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';

@Component({
    selector: 'jhi-germplasm-import-inventory',
    templateUrl: './germplasm-import-inventory.component.html'
})
export class GermplasmImportInventoryComponent implements OnInit {

    dataBackupPrev = [];

    STOCK_ID_PREFIX_REGEX = '(^\\w*[a-zA-Z]$|^$)';

    createInventoryLots = true;
    stockIdPrefix: string;

    locationSelected: number;

    units: Promise<InventoryUnit[]>;
    unitSelected: string;

    deposit: any;

    completeAllEntries = true;

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
        this.loadUnits();
        this.deposit = { amount: null };
        this.createInventoryLots = this.hasSomeInventoryDetails();
    }

    async next() {
        await this.fillData();

        this.modal.close();
        this.context.dataBackup.push(this.dataBackupPrev);
        const modalRef = this.modalService.open(GermplasmImportReviewComponent as Component,
            { windowClass: 'modal-autofit', backdrop: 'static' });
    }

    async fillData() {
        // clean up in case someone accidentally add column in spreadsheet
        this.context.data.forEach((row) => row[HEADERS['STOCK ID PREFIX']] = '');

        if (this.createInventoryLots) {
            const rows = this.context.data.filter((row) => {
                if (this.completeAllEntries) {
                    return true;
                }
                return row[HEADERS['STOCK ID']]
                    || row[HEADERS['STORAGE LOCATION ABBR']]
                    || row[HEADERS['UNITS']]
                    || row[HEADERS['AMOUNT']];
            });

            this.context.stockIdPrefix = this.stockIdPrefix;

            await this.locationService.getLocationById(this.locationSelected).toPromise().then((location) => {
                rows.forEach((row) => {
                    if (!row[HEADERS['STORAGE LOCATION ABBR']]) {
                        row[HEADERS['STORAGE LOCATION ABBR']] = location.abbreviation;
                    }

                    if (!row[HEADERS['STOCK ID']]) {
                        row[HEADERS['STOCK ID PREFIX']] = this.stockIdPrefix;
                    }

                    if (!row[HEADERS.UNITS]) {
                        row[HEADERS.UNITS] = this.unitSelected;
                    }

                    if (!row[HEADERS.AMOUNT]) {
                        row[HEADERS.AMOUNT] = this.deposit.amount;
                    }
                });
            });



        } else {
            this.context.data.forEach((row) => {
                row[HEADERS['STOCK ID']] = '';
                row[HEADERS['STORAGE LOCATION ABBR']] = '';
                row[HEADERS['UNITS']] = '';
                row[HEADERS['AMOUNT']] = '';
            });
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
        const modalRef = this.modalService.open(GermplasmImportBasicDetailsComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    canProceed(f) {
        const form = f.form;
        return !this.createInventoryLots
            ||  (form.valid
                && (this.stockIdPrefix || this.hasAllStockIds())
                && (this.locationSelected || this.hasAllLocations())
                && (this.unitSelected || this.hasAllUnits())
                && (this.deposit.amount || this.hasAllAmounts())
                );
    }

    hasAllInventoryDetails() {
        return this.hasAllStockIds() && this.hasAllLocations() && this.hasAllUnits() && this.hasAllAmounts();
    }

    enableOptionsToComplete() {
        return !this.hasAllInventoryDetails() && this.hasSomeInventoryDetails();
    }

    hasSomeInventoryDetails() {
        return this.context.data.some((row) => {
            return row[HEADERS['STOCK ID']]
                || row[HEADERS['STORAGE LOCATION ABBR']]
                || row[HEADERS['UNITS']]
                || row[HEADERS['AMOUNT']];
        });
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
