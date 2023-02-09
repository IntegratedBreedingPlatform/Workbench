import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { BreedingMethodClass } from '../../shared/breeding-method/model/breeding-method-class.model';
import { BreedingMethodGroup } from '../../shared/breeding-method/model/breeding-method-group.model';
import { BreedingMethodType } from '../../shared/breeding-method/model/breeding-method-type.model';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-observation-details',
    templateUrl: './observation-details.component.html'
})
export class ObservationDetailsComponent implements OnInit {

    @Input() public observationUnitId: number;
    editable = false;

    constructor(private route: ActivatedRoute,
                public activeModal: NgbActiveModal,
                private jhiLanguageService: JhiLanguageService) {
    }

    ngOnInit(): void {
        (<any>window).onCloseModal = this.clear;
    }


    clear() {
        this.activeModal.dismiss('cancel');
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }
}

@Component({
    selector: 'jhi-observation-details-popup',
    template: ``
})
export class ObservationDetailsPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const obsUnitId = this.route.snapshot.paramMap.get('observationUnitId');

        const modal = this.popupService.open(ObservationDetailsComponent as Component);
        modal.then((modalRef) => {
            modalRef.componentInstance.observationUnitId = obsUnitId;
        });
    }

}
