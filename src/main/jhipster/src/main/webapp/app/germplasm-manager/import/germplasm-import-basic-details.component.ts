import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportComponent, HEADERS } from './germplasm-import.component';
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
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';

@Component({
    selector: 'jhi-germplasm-import-basic-details',
    templateUrl: './germplasm-import-basic-details.component.html'
})
export class GermplasmImportBasicDetailsComponent implements OnInit {

    @ViewChild('detailsForm')
    detailsForm: ElementRef;

    data: any;
    dataBackup: any;
    nameTypes: NameType[];
    attributes: Attribute[];
    nameColumnsWithData = {};

    breedingMethods: Promise<BreedingMethod[]>;
    favoriteBreedingMethods: Promise<BreedingMethod[]>;
    breedingMethodSelected: string;
    useFavoriteBreedingMethods = true;

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService,
        private breedingMethodService: BreedingMethodService,
        private sanitizer: DomSanitizer,
        private paramContext: ParamContext,
        private popupService: PopupService
    ) {
    }

    ngOnInit(): void {
        this.dataBackup = this.data.map((row) => Object.assign({}, row));
        this.loadBreedingMethods();
    }

    next() {
        this.fillData();

        this.modal.close();
        const modalRef = this.modalService.open(GermplasmImportInventoryComponent as Component,
            { size: 'lg', backdrop: 'static' });
        modalRef.componentInstance.data = this.data
        modalRef.componentInstance.dataBackupPrev = this.dataBackup
    }

    fillData() {
        this.data.filter((row) => !row[HEADERS['BREEDING METHOD']])
            .forEach((row) => row[HEADERS['BREEDING METHOD']] = this.breedingMethodSelected);
        // TODO Complete
    }

    dismiss() {
        this.modal.dismiss();
    }

    back() {
        this.modal.close();
        const modalRef = this.modalService.open(GermplasmImportComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    loadBreedingMethods() {
        this.breedingMethods = this.breedingMethodService.getBreedingMethods().toPromise();
        this.favoriteBreedingMethods = this.breedingMethodService.getBreedingMethods(true).toPromise();
    }

    openBreedingMethodManager() {

        const params = '?programId=' + this.paramContext.selectedProjectId;

        const modal = this.popupService.open(BreedingMethodManagerComponent as Component, { windowClass: 'modal-autofit' });
        modal.then((modalRef) => {
            modalRef.componentInstance.safeUrl =
                this.sanitizer.bypassSecurityTrustResourceUrl(BREEDING_METHODS_BROWSER_DEFAULT_URL + params);
            modalRef.result.then(() => this.loadBreedingMethods());
        });
    }

    hasAllBreedingMethods() {
        return this.data.every((row) => row[HEADERS['BREEDING METHOD']]);
    }

    hasAllBasicDetails() {
        // TODO complete
        return this.hasAllBreedingMethods();
    }

    hasAllNames() {
        return true;
    }

    canProceed() {
        return this.isFormValid() || (
            this.hasAllBasicDetails() && this.hasAllNames()
        );
    }

    private isFormValid() {
        return this.breedingMethodSelected;
    }
}
