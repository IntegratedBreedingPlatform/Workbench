import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BreedingMethod } from '../../../shared/breeding-method/model/breeding-method';
import { BreedingMethodService } from '../../../shared/breeding-method/service/breeding-method.service';
import { GermplasmProgenitorsContext } from './germplasm-progenitors.context';
import { GermplasmProgenitorsDetails } from '../../../shared/germplasm/model/germplasm.model';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { ParamContext } from '../../../shared/service/param.context';
import { Subscription } from 'rxjs';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';

@Component({
    selector: 'jhi-germplasm-progenitors-modal',
    templateUrl: './germplasm-progenitors-modal.component.html'
})
export class GermplasmProgenitorsModalComponent implements OnInit, OnDestroy {

    gid: number;
    progenitorsDetails: GermplasmProgenitorsDetails;
    isLoading: boolean;
    isGenerative: boolean;
    generativeBreedingMethods: BreedingMethod[];
    derivativeBreedingMethods: BreedingMethod[];
    breedingMethodSelected: BreedingMethod;
    useFavoriteBreedingMethods = true;
    femaleParent: string;
    maleParent: string;

    eventSubscriber: Subscription;
    selectorTarget: string;

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private breedingMethodService: BreedingMethodService,
                private germplasmProgenitorsContext: GermplasmProgenitorsContext,
                private germplasmService: GermplasmService,
                private alertService: JhiAlertService,
                private router: Router,
                private paramContext: ParamContext,
                private modalService: NgbModal) {
        this.progenitorsDetails = this.germplasmProgenitorsContext.germplasmProgenitorsDetails;
    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
        this.loadBreedingMethods();
        this.initializeForm();
        this.registerGermplasmSelectorSelected();
    }

    registerGermplasmSelectorSelected() {
        this.eventSubscriber = this.eventManager.subscribe('germplasmSelectorSelected', (event) => {
            if (this.selectorTarget === 'female') {
                this.femaleParent = event.content;
            } else if (this.selectorTarget === 'male') {
                this.maleParent = event.content;
            }
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        const maleParentsList = this.maleParent.split(',').map(item => Number(item));

        if (this.isNumberOutOfRange([Number(this.femaleParent)])) {
            this.alertService.error('germplasm-progenitors-modal.validation.gid.out.of.range', { param: 'Female Parent or Group Source' });
            return;
        }

        if (this.isNumberOutOfRange(maleParentsList)) {
            this.alertService.error('germplasm-progenitors-modal.validation.gid.out.of.range', { param: 'Male Parent or Immediate Source' });
            return;
        }

        if (!this.allowMultipleMaleParents() && maleParentsList.length > 1) {
            this.alertService.error('germplasm-progenitors-modal.validation.only.one.male.parent.is.allowed', { param: this.breedingMethodSelected.name });
            return;
        }

        if (!this.isGenerative && this.progenitorsDetails.numberOfDerivativeProgeny > 0
            && (this.progenitorsDetails.breedingMethodType === 'GEN' || this.hasProgenitorsChanges())) {
            const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
            confirmModalRef.componentInstance.title = 'Update Germplasm Progenitors';
            confirmModalRef.componentInstance.message = 'Germplasm has derivative progeny and the group source will change. ' +
                'Group source change will be applied to the progeny (' + this.progenitorsDetails.numberOfDerivativeProgeny + ' germplasm). Are you sure you want to continue?';
            confirmModalRef.result.then(() => {
                this.updateGermplasmProgenitors(maleParentsList);
                this.activeModal.close();
            }, () => this.activeModal.dismiss());
        } else {
            this.updateGermplasmProgenitors(maleParentsList);
        }
    }

    hasProgenitorsChanges() {
        //Compare the maleParentIds without splitting the string since only one germplasm is allowed as male parent for non-generative germplasm
        return this.getFemaleParentId(this.progenitorsDetails) !== this.femaleParent || this.getMaleParentId(this.progenitorsDetails) !== this.maleParent;
    }

    updateGermplasmProgenitors(maleParentsList) {
        this.isLoading = true;
        const firstMaleElement = maleParentsList.shift();
        this.germplasmService.updateGermplasmProgenitors(this.gid, {
            breedingMethodId: this.breedingMethodSelected.mid,
            gpid1: Number(this.femaleParent),
            gpid2: !this.isMutation() ? firstMaleElement : 0,
            otherProgenitors: maleParentsList
        }).toPromise().then((result) => {
            this.alertService.success('germplasm-progenitors-modal.edit.success');
            this.notifyChanges();
            this.isLoading = false;
        }).catch((response) => {
            this.alertService.error('error.custom', { param: response.error.errors[0].message });
            this.isLoading = false;
        });
    }


    isFormValid(f) {
        if (this.isMutation() && !this.femaleParent) {
            return false;
        }
        return f.form.valid && !this.isLoading && this.breedingMethodSelected && this.femaleParent && this.maleParent;
    }

    initializeForm() {
        this.isGenerative = this.progenitorsDetails.breedingMethodType === 'GEN';
        this.femaleParent = this.getFemaleParentId(this.progenitorsDetails);
        this.maleParent = this.getMaleParentId(this.progenitorsDetails);
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'progenitorsChanged' });
        this.clear();
    }

    breedingMethodOptionChanged() {
        this.breedingMethodSelected = null;
    }

    getFemaleParentId(progenitorsDetails: GermplasmProgenitorsDetails) {
        if (progenitorsDetails.femaleParent) {
            return String(this.progenitorsDetails.femaleParent.gid);
        } else if (progenitorsDetails.groupSource) {
            return String(this.progenitorsDetails.groupSource.gid);
        }
        // Return 0 if unknown
        return '0';
    }

    getMaleParentId(progenitorsDetails: GermplasmProgenitorsDetails) {
        if (progenitorsDetails.maleParents) {
            return this.progenitorsDetails.maleParents.map((parent) => parent.gid).join(',');
        } else if (progenitorsDetails.immediateSource) {
            return String(this.progenitorsDetails.immediateSource.gid);
        }
        // Return 0 if unknown
        return '0';
    }

    isMutation(): boolean {
        return this.breedingMethodSelected && this.breedingMethodSelected.numberOfProgenitors === 1;
    }

    allowMultipleMaleParents(): boolean {
        return this.breedingMethodSelected && this.breedingMethodSelected.type === 'GEN' && this.breedingMethodSelected.numberOfProgenitors === 0;
    }

    loadBreedingMethods() {
        this.breedingMethodService.getBreedingMethods(false, ['GEN']).toPromise().then((result) => {
            this.generativeBreedingMethods = result;
            return this.breedingMethodService.getBreedingMethods(false, ['DER', 'MAN']).toPromise();
        }).then((result) => {
            this.derivativeBreedingMethods = result;
            this.breedingMethodSelected = (this.isGenerative) ? this.generativeBreedingMethods.find((item) => item.mid === this.progenitorsDetails.breedingMethodId) :
                this.derivativeBreedingMethods.find((item) => item.mid === this.progenitorsDetails.breedingMethodId);
        });
    }

    /* Return true or false if it is the selected */
    compareById(idFist, idSecond): boolean {
        return idFist && idSecond && idFist.mid === idSecond.mid;
    }

    openGermplasmSelector(selectMultiple: boolean, target: string): void {
        this.selectorTarget = target;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-selector-dialog' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                cropName: this.paramContext.cropName,
                loggedInUserId: this.paramContext.loggedInUserId,
                programUUID: this.paramContext.programUUID,
                authToken: this.paramContext.authToken,
                selectMultiple: selectMultiple
            }
        });
    }

    isNumberOutOfRange(numbers: Number[]) {
        return numbers.some(num => num > 2147483647);
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
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
        });
    }

}
