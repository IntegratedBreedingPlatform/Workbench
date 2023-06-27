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
import { finalize } from 'rxjs/internal/operators/finalize';

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
        this.isLoading = true;
        if (this.locationId) {
            this.locationService.updateLocation(this.locationRequest, this.locationId).pipe(
                finalize(() => this.isLoading = false)
            ).subscribe(() => {
                this.alertService.success('crop-settings-manager.location.modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }, (error) => this.onError(error));
        } else {
            this.locationService.createLocation(this.locationRequest).pipe(
                finalize(() => this.isLoading = false)
            ).subscribe(() => {
                this.alertService.success('crop-settings-manager.location.modal.create.success');
                this.notifyChanges();
                this.isLoading = false;
            }, (error) => this.onError(error));
        }
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.locationRequest.name && this.locationRequest.type
            && this.locationRequest.abbreviation;
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'locationViewChanged', content: '' });
        this.clear();
    }

    ngOnInit(): void {
        this.locationService.getCountries()
            .subscribe(
                (resp: HttpResponse<Location[]>) => {
                    this.countries = resp.body;
                    this.countries.unshift(this.nonSpecifyLocation);
                    if (this.cropSettingsContext.location) {
                        this.locationRequest.countryId = this.cropSettingsContext.location.countryId;
                    }
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
                        this.provinces.unshift(this.nonSpecifyLocation);
                        this.locationRequest.provinceId = this.cropSettingsContext.location.provinceId;
                    },
                    (res: HttpErrorResponse) => this.onError(res)
                );
        }
        this.locationService.getLocationTypes(true).toPromise().then((locationTypes: LocationType[]) => {
            this.locationTypes = locationTypes;
            if (this.cropSettingsContext.location) {
                this.locationRequest.type = this.cropSettingsContext.location.type;
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
