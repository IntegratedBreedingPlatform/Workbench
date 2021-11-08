import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { LocationService } from '../../shared/location/service/location.service';
import { Location } from '../../shared/location/model/location';
import { LocationType } from '../../shared/location/model/location-type';
import { LocationModel, LocationTypeEnum } from '../../shared/location/model/location.model';

@Component({
    selector: 'jhi-location',
    templateUrl: './location.component.html'
})
export class LocationComponent implements OnInit {

    @Input() public locationId: number;
    breedingLocation: Location = new Location();
    locationTypes: LocationType[] = [];
    countries: LocationModel[] = [];
    provinces: LocationModel[] = [];
    selectedCountry: LocationModel;
    selectedProvince: LocationModel;
    selectedLocationType: LocationType;
    editable = false;

    constructor(public activeModal: NgbActiveModal,
                public locationService: LocationService) {
    }

    ngOnInit(): void {
        this.locationService.queryBreedingLocation(this.locationId).toPromise().then((breedingLocation) => {
            this.breedingLocation = breedingLocation;
        }).then(() => {
            this.locationService.queryLocationsByType([LocationTypeEnum.COUNTRY], false).toPromise().then((resp) => {
                const locations = resp.body;
                this.countries = locations;
                this.selectedCountry = locations.find((e) => e.id === this.breedingLocation.countryId);
            });
            this.locationService.queryLocationsByType([LocationTypeEnum.PROVINCE], false).toPromise().then((resp) => {
                const locations = resp.body;
                this.provinces = locations;
                this.selectedProvince = locations.find((e) => e.id === this.breedingLocation.provinceId);
            });
            this.locationService.queryLocationTypes().toPromise().then((locationTypes) => {
                this.locationTypes = locationTypes;
                this.selectedLocationType = locationTypes.find((e) => e.id === this.breedingLocation.type);
            });
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
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
