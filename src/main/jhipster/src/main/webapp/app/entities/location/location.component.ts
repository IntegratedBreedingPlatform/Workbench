import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { LocationService } from '../../shared/location/service/location.service';
import { BreedingLocationModel } from '../../shared/location/model/breeding-location.model';
import { LocationType } from '../../shared/location/model/location-type.model';
import { LocationModel, LocationTypeEnum } from '../../shared/location/model/location.model';

@Component({
    selector: 'jhi-location',
    templateUrl: './location.component.html'
})
export class LocationComponent implements OnInit {

    @Input() public locationId: number;
    breedingLocation: BreedingLocationModel = new BreedingLocationModel();
    locationTypes: LocationType[] = [];
    countries: LocationModel[] = [];
    provinces: LocationModel[] = [];
    selectedCountry: LocationModel;
    selectedProvince: LocationModel;
    selectedLocationType: LocationType;
    accessible = false;
    editable = false;

    constructor(public activeModal: NgbActiveModal,
                public locationService: LocationService) {
    }

    ngOnInit(): void {
        this.locationService.queryBreedingLocation(this.locationId).toPromise().then((breedingLocation) => {
            this.breedingLocation = breedingLocation;
            this.accessible = breedingLocation.programUUID === '';
        }).then(() => {
            this.locationService.queryLocationsByType([LocationTypeEnum.COUNTRY], false).toPromise().then((locations) => {
                this.countries = locations;
                this.selectedCountry = locations.find((e) => e.id === this.breedingLocation.countryId);
            })
            this.locationService.queryLocationsByType([LocationTypeEnum.PROVINCE], false).toPromise().then((locations) => {
                this.provinces = locations;
                this.selectedProvince = locations.find((e) => e.id === this.breedingLocation.provinceId);
            })
            this.locationService.queryLocationTypes().toPromise().then((locationTypes) => {
                this.locationTypes = locationTypes;
                this.selectedLocationType = locationTypes.find((e) => e.id === this.breedingLocation.type);
            })
        })
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
