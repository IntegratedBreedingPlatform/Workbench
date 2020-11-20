import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportComponent } from './germplasm-import.component';
import { Attribute } from '../../shared/attributes/model/attribute.model';
import { NameType } from '../../shared/germplasm/model/name-type.model';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { Crop } from '../../../../../../../web/src/appsNg2/admin/app/shared/models/crop.model';
import { PipeTransform } from '@angular/core';
import { Select2OptionData } from 'ng-select2';
import { Pipe } from '@angular/core';
import { BREEDING_METHODS_BROWSER_DEFAULT_URL } from '../../app.constants';
import { BreedingMethodManagerComponent } from '../../entities/breeding-method/breeding-method-manager.component';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { DomSanitizer } from '@angular/platform-browser';
import { GermplasmImportBasicDetailsComponent } from './germplasm-import-basic-details.component';
import { GermplasmImportReviewComponent } from './germplasm-import-review.component';
import { GermplasmImportContext } from './germplasm-import.context';

@Component({
    selector: 'jhi-germplasm-import-inventory',
    templateUrl: './germplasm-import-inventory.component.html'
})
export class GermplasmImportInventoryComponent implements OnInit {

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private popupService: PopupService,
        public context: GermplasmImportContext
    ) {
    }

    ngOnInit(): void {
        this.context.dataBackup = this.context.data.map((row) => Object.assign({}, row));
    }

    next() {
        this.modal.close();
        this.context.dataBackupPrev = this.context.dataBackup;
        const modalRef = this.modalService.open(GermplasmImportReviewComponent as Component,
            { windowClass: 'modal-autofit', backdrop: 'static' });
    }

    dismiss() {
        this.modal.dismiss();
    }

    back() {
        this.modal.close();
        this.context.data = this.context.dataBackupPrev;
        const modalRef = this.modalService.open(GermplasmImportBasicDetailsComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    isValid() {
        // TODO
        return true;
    }
}
