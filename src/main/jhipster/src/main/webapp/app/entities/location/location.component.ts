import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { LocationService } from '../../shared/location/service/location.service';
import { Location } from '../../shared/location/model/location';
import { LocationType } from '../../shared/location/model/location-type';
import { LocationTypeEnum } from '../../shared/location/model/location-type.enum';
import { LocationSearchRequest } from '../../shared/location/model/location-search-request.model';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { MAX_PAGE_SIZE } from '../../app.constants';

@Component({
    selector: 'jhi-location',
    templateUrl: './location.component.html'
})
export class LocationComponent implements OnInit {

    @Input() public locationId: number;

    breedingLocation: Location = new Location();
    locationTypes: LocationType[] = [];
    countries: Location[] = [];
    provinces: Location[] = [];
    selectedCountry: Location;
    selectedProvince: Location;
    selectedLocationType: LocationType;
    editable = false;

    constructor(public activeModal: NgbActiveModal,
                public locationService: LocationService,
                private alertService: AlertService) {
    }

    ngOnInit(): void {
        this.locationService.getLocationById(this.locationId).toPromise().then((breedingLocation: Location) => {
            this.breedingLocation = breedingLocation;
        }).then(() => {
            if (this.editable) {
                const pagination = {
                    page: 0,
                    size: MAX_PAGE_SIZE
                };

                const countriesSearchRequest: LocationSearchRequest = new LocationSearchRequest();
                countriesSearchRequest.locationTypeIds = [LocationTypeEnum.COUNTRY];
                this.locationService.searchLocations(countriesSearchRequest, false, pagination)
                    .subscribe(
                        (resp: HttpResponse<Location[]>) => { this.countries = resp.body; },
                        (res: HttpErrorResponse) => this.onError(res)
                    );

                const provincesSearchRequest: LocationSearchRequest = new LocationSearchRequest();
                provincesSearchRequest.locationTypeIds = [LocationTypeEnum.PROVINCE];
                this.locationService.searchLocations(provincesSearchRequest, false, pagination)
                    .subscribe(
                        (resp: HttpResponse<Location[]>) => { this.provinces = resp.body; },
                        (res: HttpErrorResponse) => this.onError(res)
                    );
            } else {
                if (this.breedingLocation.countryId) {
                    this.locationService.getLocationById(this.breedingLocation.countryId).toPromise().then((resp: Location) => {
                        this.selectedCountry = resp;
                    });
                }

                if (this.breedingLocation.provinceId) {
                    this.locationService.getLocationById(this.breedingLocation.provinceId).toPromise().then((resp: Location) => {
                        this.selectedProvince = resp;
                    });
                }
            }

            this.locationService.getLocationTypes(false).toPromise().then((locationTypes: LocationType[]) => {
                this.locationTypes = locationTypes;
                this.selectedLocationType = locationTypes.find((e) => e.id === this.breedingLocation.type);
            });
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
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
    selector: 'jhi-location-popup',
    template: ``
})
export class LocationPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const locationId = this.route.snapshot.paramMap.get('locationId');

        const modal = this.popupService.open(LocationComponent as Component);
        modal.then((modalRef) => {
            modalRef.componentInstance.locationId = locationId;
        });
    }

}
