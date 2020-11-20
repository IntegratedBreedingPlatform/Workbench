import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';
import { GermplasmImportContext } from './germplasm-import.context';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { GermplasmList } from '../../shared/model/germplasm-list';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmImportRequest } from '../../shared/germplasm/model/germplasm-import-request.model';
import { HEADERS } from './germplasm-import.component';

@Component({
    selector: 'jhi-germplasm-import-review',
    templateUrl: './germplasm-import-review.component.html'
})
export class GermplasmImportReviewComponent implements OnInit {

    isLoading: boolean;

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private popupService: PopupService,
        private alertService: AlertService,
        private germplasmService: GermplasmService,
        public context: GermplasmImportContext
    ) {
    }

    ngOnInit(): void {
        this.context.dataBackup = this.context.data.map((row) => Object.assign({}, row));
    }

    save() {
        this.isLoading = true;
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
            this.isLoading = false;
        })).subscribe(
            (res: GermplasmList) => this.onSaveSuccess(res),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSaveSuccess(res: GermplasmList) {
        this.alertService.success('germplasm-list-creation.success');
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
        this.context.dataBackupPrev = this.context.dataBackup;
        const modalRef = this.modalService.open(GermplasmImportInventoryComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }
}
