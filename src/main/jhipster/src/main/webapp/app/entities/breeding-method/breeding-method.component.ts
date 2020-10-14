import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { BreedingMethodClass } from '../../shared/breeding-method/model/breeding-method-class.model';
import { BreedingMethodGroup } from '../../shared/breeding-method/model/breeding-method-group.model';
import { BreedingMethodType } from '../../shared/breeding-method/model/breeding-method-type.model';

@Component({
    selector: 'jhi-breeding-method',
    templateUrl: './breeding-method.component.html'
})
export class BreedingMethodComponent implements OnInit {

    @Input() public breedingMethodId: number;
    breedingMethod: BreedingMethod = new BreedingMethod();
    breedingMethodClasses: BreedingMethodClass[] = [];
    breedingMethodGroups: BreedingMethodGroup[] = [];
    breedingMethodTypes: BreedingMethodType[] = [];
    selectedBreedingMethodType: BreedingMethodType;
    selectedBreedingMethodClass: BreedingMethodClass;
    selectedBreedingMethodGroup: BreedingMethodGroup;
    editable = false;

    constructor(public activeModal: NgbActiveModal,
                public breedingMethodService: BreedingMethodService) {
    }

    ngOnInit(): void {
        this.breedingMethodService.queryBreedingMethod(this.breedingMethodId).toPromise().then((breedingMethod) => {
            this.breedingMethod = breedingMethod;
        }).then(() => {
            this.breedingMethodService.queryBreedingMethodClasses().toPromise().then((breedingMethodClasses) => {
                this.breedingMethodClasses = breedingMethodClasses;
                this.selectedBreedingMethodClass = breedingMethodClasses.find((e) => e.id === this.breedingMethod.methodClass);
            })
            this.breedingMethodService.queryBreedingMethodGroups().toPromise().then((breedingMethodGroups) => {
                this.breedingMethodGroups = breedingMethodGroups;
                this.selectedBreedingMethodGroup = breedingMethodGroups.find((e) => e.code === this.breedingMethod.group);
            })
            this.breedingMethodService.queryBreedingMethodTypes().toPromise().then((breedingMethodTypes) => {
                this.breedingMethodTypes = breedingMethodTypes;
                this.selectedBreedingMethodType = breedingMethodTypes.find((e) => e.code === this.breedingMethod.type);
            })
        })

    }

    clear() {
        this.activeModal.dismiss('cancel');
    }
}

@Component({
    selector: 'jhi-breeding-method-popup',
    template: ``
})
export class BreedingMethodPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const breedingMethodId = this.route.snapshot.paramMap.get('breedingMethodId');

        const modal = this.popupService.open(BreedingMethodComponent as Component);
        modal.then((modalRef) => {
            modalRef.componentInstance.breedingMethodId = breedingMethodId;
        });
    }

}
