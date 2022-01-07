import { Component, OnDestroy, OnInit } from '@angular/core';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { AlertService } from '../../shared/alert/alert.service';
import { CropSettingsContext } from '../crop-Settings.context';
import { LocationService } from '../../shared/location/service/location.service';
import { LocationType } from '../../shared/location/model/location-type';
import { Location } from '../../shared/location/model/location';
import { MAX_PAGE_SIZE } from '../../app.constants';
import { LocationSearchRequest } from '../../shared/location/model/location-search-request.model';
import { LocationTypeEnum } from '../../shared/location/model/location-type.enum';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';

@Component({
    selector: 'jhi-location-edit-dialog',
    templateUrl: './location-edit-dialog.component.html'
})
export class LocationEditDialogComponent implements OnInit, OnDestroy {

    PAGINATION = {
        page: 0,
        size: MAX_PAGE_SIZE
    };

    NON_SPECIFY_LOCATION_ID = '-1';

    locationId: number;
    name: string;
    abbreviation: string;
    locationTypeId: number;
    countryId: number;
    provinceId: number;
    isLoading: boolean;
    locationRequest: any;

    locationTypes: LocationType[] = [];
    countries: Location[] = [];
    provinces: Location[] = [];

    selectedCountry: Location;
    selectedProvince: Location;
    selectedLocationType: LocationType;

    nonSpecifyLocation: Location = new Location(-1);
    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private locationService: LocationService,
                private alertService: AlertService,
                private cropSettingsContext: CropSettingsContext) {

        this.locationRequest = {
            name: null, abbreviation: null, type: null, countryId: null,
            provinceId: null, latitude: null, longitude: null, altitude: null
        };
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {

    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.locationRequest.name && this.locationRequest.type
            && this.locationRequest.abbreviation;
    }

    notifyChanges(): void {

    }

    ngOnInit(): void {

    }

    countryChanged(): void {

    }

    provinceChanged(): void {
    }

    ngOnDestroy(): void {
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }
}

@Component({
    selector: 'jhi-location-edit-popup',
    template: ''
})
export class LocationEditPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        this.popupService.open(LocationEditDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }
}
