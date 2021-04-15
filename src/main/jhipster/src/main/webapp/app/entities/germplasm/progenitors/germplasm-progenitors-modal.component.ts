import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {PopupService} from "../../../shared/modal/popup.service";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {GermplasmProgenitorsDetails} from "../../../shared/germplasm/model/germplasm.model";
import {BreedingMethod} from "../../../shared/breeding-method/model/breeding-method";
import {BreedingMethodService} from "../../../shared/breeding-method/service/breeding-method.service";
import {GermplasmProgenitorsContext} from "./germplasm-progenitors.context";
@Component({
    selector: 'jhi-germplasm-progenitors-modal',
    templateUrl: './germplasm-progenitors-modal.component.html'
})
export class GermplasmProgenitorsModalComponent implements OnInit, OnDestroy {

    isEditMode: boolean;
    isLoading: boolean;
    germplasmProgenitorsDetails: GermplasmProgenitorsDetails;
    breedingMethods: BreedingMethod[];
    constructor(public activeModal: NgbActiveModal,
                private breedingMethodService: BreedingMethodService,
                private germplasmProgenitors: GermplasmProgenitorsContext) {
        this.germplasmProgenitorsDetails = germplasmProgenitors.germplasmProgenitorsDetails;
    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
        this.breedingMethodService.getBreedingMethods().toPromise().then((breedingMethods: BreedingMethod[]) => this.breedingMethods = breedingMethods)
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading;
    }
}

@Component({
    selector: 'jhi-germplasm-progenitors-popup',
    template: ``
})
export class GermplasmProgenitorsPopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmProgenitorsModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' })
    }

}