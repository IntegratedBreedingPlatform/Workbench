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
import { GermplasmImportContext } from './germplasm-import.context';

@Component({
    selector: 'jhi-germplasm-import-basic-details',
    templateUrl: './germplasm-import-basic-details.component.html'
})
export class GermplasmImportBasicDetailsComponent implements OnInit {

    @ViewChild('detailsForm')
    detailsForm: ElementRef;

    hasEmptyPreferredName: boolean;
    // Codes that are both attributes
    unmapped = [];
    draggedCode: string;

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
        private popupService: PopupService,
        public context: GermplasmImportContext
    ) {
    }

    ngOnInit(): void {
        this.context.dataBackup = this.context.data.map((row) => Object.assign({}, row));
        this.loadBreedingMethods();

        this.hasEmptyPreferredName = this.context.data.some((row) => !row[HEADERS['PREFERRED NAME']]);

        for (const attribute of this.context.attributes) {
            for (const nameType of this.context.nameTypes) {
                if (attribute.code === nameType.code) {
                    this.unmapped.push(attribute.code);
                }
            }
        }
        this.context.nametypesCopy = this.context.nameTypes.filter((name) => {
            return this.context.nameColumnsWithData[name.code] && this.unmapped.indexOf(name.code) === -1;
        });
        this.context.attributesCopy = this.context.attributes.filter((attribute) => {
            return this.unmapped.indexOf(attribute.code) === -1;
        });
    }

    next() {
        this.fillData();

        this.modal.close();
        this.context.dataBackupPrev = this.context.dataBackup;
        const modalRef = this.modalService.open(GermplasmImportInventoryComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    fillData() {
        this.context.data.filter((row) => !row[HEADERS['BREEDING METHOD']])
            .forEach((row) => row[HEADERS['BREEDING METHOD']] = this.breedingMethodSelected);

        dataLoop: for (const row of this.context.data) {
            if (!row[HEADERS['PREFERRED NAME']]) {
                // names already ordered by priority
                for (const name of this.context.nametypesCopy) {
                    if (row[name.code]) {
                        row[HEADERS['PREFERRED NAME']] = name.code;
                        continue dataLoop;
                    }
                }
            }
        }

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
        return this.context.data.every((row) => row[HEADERS['BREEDING METHOD']]);
    }

    hasAllBasicDetails() {
        // TODO complete
        return this.hasAllBreedingMethods();
    }

    canProceed() {
        return this.isFormValid() || (
            this.hasAllBasicDetails()
                && this.unmapped.length === 0
        );
    }

    private isFormValid() {
        // TODO complete
        return this.breedingMethodSelected;
    }

    dragStart($event, code) {
        this.draggedCode = code;
    }

    dragEnd($event) {
        this.draggedCode = null;
    }

    drop($event, type: 'names' | 'attributes') {
        if (type === 'names') {
            this.context.nametypesCopy.push(this.context.nameTypes.find((n) => n.code === this.draggedCode));
        } else {
            this.context.attributesCopy.push(this.context.attributes.find((a) => a.code === this.draggedCode));
        }
        this.unmapped = this.unmapped.filter((u) => u !== this.draggedCode);
    }

}
