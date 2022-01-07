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
        if (this.locationId) {
            this.isLoading = true;
            this.locationService.updateLocation(this.locationRequest, this.locationId).toPromise().then((result) => {
                this.alertService.success('crop-settings-manager.location.modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        } else {
            this.isLoading = true;
            this.locationService.createLocation(this.locationRequest).toPromise().then((result) => {
                this.alertService.success('crop-settings-manager.location.modal.create.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        }
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.locationRequest.name && this.locationRequest.type
            && this.locationRequest.abbreviation;
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'locationViewChanged' });
        this.clear();
    }

    ngOnInit(): void {
        const countriesSearchRequest: LocationSearchRequest = new LocationSearchRequest();
        countriesSearchRequest.locationTypeIds = [LocationTypeEnum.COUNTRY];
        this.locationService.searchLocations(countriesSearchRequest, false, this.PAGINATION)
            .subscribe(
                (resp: HttpResponse<Location[]>) => {
                    this.countries = resp.body;
                    if (this.cropSettingsContext.location) {
                        this.selectedCountry = resp.body.find((e) => e.id === this.cropSettingsContext.location.countryId);
                        this.locationRequest.countryId = this.selectedCountry.id;
                    }
                    this.countries.unshift(this.nonSpecifyLocation);
                },
                (res: HttpErrorResponse) => this.onError(res)
            );

        if (this.cropSettingsContext.location && this.cropSettingsContext.location.countryId) {
            const provincesSearchRequest: LocationSearchRequest = new LocationSearchRequest();
            provincesSearchRequest.countryIds = [this.cropSettingsContext.location.countryId];
            provincesSearchRequest.locationTypeIds = [LocationTypeEnum.PROVINCE];
            this.locationService.searchLocations(provincesSearchRequest, false, this.PAGINATION)
                .subscribe(
                    (resp: HttpResponse<Location[]>) => {
                        this.provinces = resp.body;
                        this.selectedProvince = resp.body.find((e) => e.id === this.cropSettingsContext.location.provinceId);
                        this.locationRequest.provinceId = this.selectedProvince.id;
                        this.provinces.unshift(this.nonSpecifyLocation);

                    },
                    (res: HttpErrorResponse) => this.onError(res)
                );
        }
        this.locationService.getLocationTypes().toPromise().then((locationTypes: LocationType[]) => {
            this.locationTypes = locationTypes;
            if (this.cropSettingsContext.location) {
                this.selectedLocationType = locationTypes.find((e) => e.id === this.cropSettingsContext.location.type);
                this.locationRequest.type = this.selectedLocationType.id;
            }

        });

        if (this.cropSettingsContext.location) {
            this.locationId = this.cropSettingsContext.location.id;
            this.locationRequest.name = this.cropSettingsContext.location.name;
            this.locationRequest.abbreviation = this.cropSettingsContext.location.abbreviation;

            this.locationRequest.latitude = this.cropSettingsContext.location.latitude;
            this.locationRequest.longitude = this.cropSettingsContext.location.longitude;
            this.locationRequest.altitude = this.cropSettingsContext.location.altitude;

        }
    }

    countryChanged(): void {
        if (this.locationRequest.countryId !== this.NON_SPECIFY_LOCATION_ID) {
            const provincesSearchRequest: LocationSearchRequest = new LocationSearchRequest();
            provincesSearchRequest.countryIds = [this.locationRequest.countryId];
            provincesSearchRequest.locationTypeIds = [LocationTypeEnum.PROVINCE];
            this.locationService.searchLocations(provincesSearchRequest, false, this.PAGINATION)
                .subscribe(
                    (resp: HttpResponse<Location[]>) => {
                        this.provinces = resp.body;
                        this.provinces.unshift(this.nonSpecifyLocation);
                    },
                    (res: HttpErrorResponse) => this.onError(res)
                );
        } else {
            this.locationRequest.countryId = null;
            this.provinces = [];
        }
        this.locationRequest.provinceId = null;
    }

    provinceChanged(): void {
        this.locationRequest.provinceId = this.locationRequest.provinceId === this.NON_SPECIFY_LOCATION_ID ? null : this.locationRequest.provinceId;
    }

    ngOnDestroy(): void {
        this.cropSettingsContext.location = null;
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
