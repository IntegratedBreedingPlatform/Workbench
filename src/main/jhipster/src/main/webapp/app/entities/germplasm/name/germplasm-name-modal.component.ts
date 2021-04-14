import { Component, OnDestroy, OnInit } from '@angular/core';
import { GermplasmName } from '../../../shared/germplasm/model/germplasm.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { NameType } from '../../../shared/germplasm/model/name-type.model';
import { LocationModel } from '../../../shared/location/model/location.model';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { GermplasmNameContext } from './germplasm-name.context';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';

@Component({
    selector: 'jhi-germplasm-name-modal',
    templateUrl: './germplasm-name-modal.component.html'
})
export class GermplasmNameModalComponent implements OnInit, OnDestroy {


    isEditMode: boolean;
    germplasmName: GermplasmName = new GermplasmName();
    nameTypes: Promise<NameType[]>;
    locations: LocationModel[];
    isLoading: boolean;

    constructor(public activeModal: NgbActiveModal,
                private germplasmNameContext: GermplasmNameContext,
                private germplasmService: GermplasmService) {
    }

    ngOnInit(): void {
        this.germplasmName = this.germplasmNameContext.germplasmName;
        this.nameTypes = this.germplasmService.getGermplasmNameTypes([]).toPromise();
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {

    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading;
    }

    ngOnDestroy(): void {
        this.germplasmNameContext.germplasmName = new GermplasmName();
    }
}

@Component({
    selector: 'jhi-germplasm-name-popup',
    template: ``
})
export class GermplasmNamePopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmNameModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }

}
