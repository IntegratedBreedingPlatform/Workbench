import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbCalendar, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmImportComponent, HEADERS } from './germplasm-import.component';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { BREEDING_METHODS_BROWSER_DEFAULT_URL, SERVER_API_URL } from '../../app.constants';
import { BreedingMethodManagerComponent } from '../../entities/breeding-method/breeding-method-manager.component';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { DomSanitizer } from '@angular/platform-browser';
import { GermplasmImportInventoryComponent } from './germplasm-import-inventory.component';
import { GermplasmImportContext } from './germplasm-import.context';
import { Location } from '../../shared/location/model/location';
import { LocationService } from '../../shared/location/service/location.service';
import { LocationTypeEnum } from '../../shared/location/model/location.model';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { PedigreeConnectionType } from '../../shared/germplasm/model/germplasm-import-request.model';
import { isNumeric } from '../../shared/util/is-numeric';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { isValidValue } from '../../shared/ontology/util/is-valid-value';
import { DataType } from '../../shared/ontology/data-type';
import { toUpper } from '../../shared/util/to-upper';

@Component({
    selector: 'jhi-germplasm-import-basic-details',
    templateUrl: './germplasm-import-basic-details.component.html'
})
export class GermplasmImportBasicDetailsComponent implements OnInit {

    dataBackupPrev = [];

    @ViewChild('detailsForm')
    detailsForm: ElementRef;

    hasEmptyPreferredName: boolean;
    // Codes that are both attributes and names
    unmapped = [];
    draggedCode: string;
    attributeStatusById: { [key: number]: boolean; } = {};

    breedingMethods: Promise<BreedingMethod[]>;
    favoriteBreedingMethods: Promise<BreedingMethod[]>;
    breedingMethodSelected: string;

    useFavoriteBreedingMethods = true;
    locationsOptions: any;
    locationSelected: string;
    useFavoriteLocations = true;
    isBreedingAndCountryLocationsOnly = false;

    locationsFilteredItemsCount;

    creationDateSelected: NgbDate | null;

    referenceSelected: string;

    // progenitors
    PedigreeConnectionType = PedigreeConnectionType;

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService,
        private breedingMethodService: BreedingMethodService,
        private locationService: LocationService,
        private sanitizer: DomSanitizer,
        private paramContext: ParamContext,
        private popupService: PopupService,
        public context: GermplasmImportContext,
        private calendar: NgbCalendar
    ) {
        this.creationDateSelected = calendar.getToday();
    }

    ngOnInit(): void {
        this.dataBackupPrev = this.context.data.map((row) => Object.assign({}, row));
        this.loadBreedingMethods();

        this.hasEmptyPreferredName = this.context.data.some((row) => !row[HEADERS['PREFERRED NAME']]);

        for (const attribute of this.context.attributes) {
            for (const nameType of this.context.nameTypes) {
                const attributeName = attribute.alias || attribute.name;
                if (attributeName === nameType.code) {
                    this.unmapped.push(attributeName);
                }
            }
        }
        this.context.nametypesCopy = this.context.nameTypes.filter((name) => {
            return this.context.nameColumnsWithData[name.code] && this.unmapped.indexOf(name.code) === -1;
        });
        this.context.attributesCopy = this.context.attributes.filter((attribute) => {
            return this.unmapped.indexOf(attribute.alias || attribute.name) === -1;
        });
        this.computeAttributeStatus();

        if (this.context.data.some((row) => row[HEADERS['PROGENITOR 1']] || row[HEADERS['PROGENITOR 2']])) {
            if (this.context.data.some((row) =>
                row[HEADERS['PROGENITOR 1']] && !isNumeric(row[HEADERS['PROGENITOR 1']]) ||
                row[HEADERS['PROGENITOR 2']] && !isNumeric(row[HEADERS['PROGENITOR 2']]))
            ) {
                this.context.pedigreeConnectionType = PedigreeConnectionType.GUID;
            } else {
                this.context.pedigreeConnectionType = PedigreeConnectionType.GID;
            }
        }

        this.locationsOptions = {
            ajax: {
                delay: 500,
                transport: function(params, success, failure) {
                    params.data.page = params.data.page || 1;
                    const locationTypes = this.isBreedingAndCountryLocationsOnly ? [LocationTypeEnum.BREEDING_LOCATION, LocationTypeEnum.COUNTRY] : [];
                    this.locationService.queryLocationsByType(
                        locationTypes,
                        this.useFavoriteLocations,
                        params.data.term,
                        (params.data.page - 1),
                        300
                    ).subscribe((res) => {
                        this.locationsFilteredItemsCount = res.headers.get('X-Filtered-Count');
                        success(res.body);
                    }, failure);
                }.bind(this),
                processResults: function(locations, params) {
                    params.page = params.page || 1;

                    return {
                        results: locations.map((location) => {
                            return {
                                id: location.abbreviation ? location.abbreviation : location.name,
                                text: location.abbreviation ? location.name + ' - (' + location.abbreviation + ')' : location.name
                            };
                        }),
                        pagination: {
                            more: (params.page * 300) < this.locationsFilteredItemsCount
                        }
                    };
                }.bind(this)
            }
        };
    }

    next() {
        this.fillData();

        this.modal.close();
        this.context.dataBackup.push(this.dataBackupPrev);
        const modalRef = this.modalService.open(GermplasmImportInventoryComponent as Component,
            { size: 'lg', backdrop: 'static' });
    }

    fillData() {
        this.context.data.filter((row) => !row[HEADERS['BREEDING METHOD']])
            .forEach((row) => row[HEADERS['BREEDING METHOD']] = this.breedingMethodSelected);

        this.context.data.filter((row) => !row[HEADERS['LOCATION ABBR']])
            .forEach((row) => row[HEADERS['LOCATION ABBR']] = this.locationSelected);

        this.context.data.filter((row) => !row[HEADERS['CREATION DATE']])
            .forEach((row) => row[HEADERS['CREATION DATE']] = ''
                + this.creationDateSelected.year
                + ('0' + this.creationDateSelected.month).slice(-2)
                + ('0' + this.creationDateSelected.day).slice(-2));

        if (this.referenceSelected) {
            this.context.data.filter((row) => !row[HEADERS['REFERENCE']])
                .forEach((row) => row[HEADERS['REFERENCE']] = this.referenceSelected);
        }

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
    }

    dismiss() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component, { backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm.import.cancel.confirm');
        confirmModalRef.result.then(() => this.modal.dismiss());
    }

    back() {
        this.context.data = this.context.dataBackup.pop();
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

    hasAllLocations() {
        return this.context.data.every((row) => row[HEADERS['LOCATION ABBR']]);
    }

    hasAllCreationDate() {
        return this.context.data.every((row) => row[HEADERS['CREATION DATE']]);
    }

    hasAllReference() {
        return this.context.data.every((row) => row[HEADERS['REFERENCE']]);
    }

    hasAllBasicDetails() {
        return this.hasAllBreedingMethods() && this.hasAllLocations() && this.hasAllCreationDate();
    }

    hasProgenitors() {
        return this.context.data.some((row) => row[HEADERS['PROGENITOR 1']] || row[HEADERS['PROGENITOR 2']]);
    }

    canProceed(f) {
        const form = f.form;
        return form.valid && (this.breedingMethodSelected || this.hasAllBreedingMethods())
            && (this.locationSelected || this.hasAllLocations())
            && (this.creationDateSelected || this.hasAllCreationDate())
            && this.unmapped.length === 0;
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
            this.context.attributesCopy.push(this.context.attributes.find((a) => a.alias === this.draggedCode || a.name === this.draggedCode));
            this.computeAttributeStatus();
        }
        this.unmapped = this.unmapped.filter((u) => u !== this.draggedCode);
    }

    computeAttributeStatus() {
        this.context.attributesCopy.forEach((attribute) => {
            const hasSomeInvalid = this.context.data.some((row) => {
                const value = row[toUpper(attribute.alias)] || row[toUpper(attribute.name)];
                return !isValidValue(value, attribute);
            });
            this.attributeStatusById[attribute.id] = hasSomeInvalid;
        })
    }

    getStatus(attribute: VariableDetails) {
        const hasSomeInvalid = this.attributeStatusById[attribute.id];
        if (!hasSomeInvalid) {
            return '<span class="fa fa-lg fa-check-circle text-success"></span>'
        } else if (attribute.scale.dataType.name === DataType.CATEGORICAL) {
            const scale = attribute.scale.name;
            const dataType = attribute.scale.dataType.name;
            const title = this.translateService.instant('germplasm.import.basicDetails.attributes.tooltip.invalid', { scale, dataType });
            return '<span class="fa fa-lg fa-times-circle text-danger" title="' + title + '"></span>'
        } else if (attribute.scale.dataType.name === DataType.NUMERIC) {
            const min = attribute.scale.validValues && (
                attribute.scale.validValues.min || attribute.scale.validValues.min === 0)
                ? attribute.scale.validValues.min
                : attribute.expectedRange.min;
            const max = attribute.scale.validValues && (
                attribute.scale.validValues.max || attribute.scale.validValues.max === 0)
                ? attribute.scale.validValues.max
                : attribute.expectedRange.max;
            const title = this.translateService.instant('germplasm.import.basicDetails.attributes.tooltip.outOfExpected', { min, max });
            return '<span class="fa fa-lg fa-warning text-warning" title="' + title + '"></span>'
        }
    }
}
