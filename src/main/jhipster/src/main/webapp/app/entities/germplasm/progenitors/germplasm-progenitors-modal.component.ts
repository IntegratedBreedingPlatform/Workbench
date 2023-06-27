import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BreedingMethod } from '../../../shared/breeding-method/model/breeding-method';
import { GermplasmProgenitorsContext } from './germplasm-progenitors.context';
import { GermplasmProgenitorsDetails } from '../../../shared/germplasm/model/germplasm.model';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { ParamContext } from '../../../shared/service/param.context';
import { Subscription } from 'rxjs';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { BreedingMethodTypeEnum } from '../../../shared/breeding-method/model/breeding-method-type.model';
import { BreedingMethodFilterTypeEnum } from '../../../shared/breeding-methods-select/breeding-methods-select.component';

@Component({
    selector: 'jhi-germplasm-progenitors-modal',
    templateUrl: './germplasm-progenitors-modal.component.html'
})
export class GermplasmProgenitorsModalComponent implements OnInit, OnDestroy {

    private readonly UNKNOWN = '0';

    gid: number;
    progenitorsDetails: GermplasmProgenitorsDetails;
    isLoading: boolean;
    isGenerative: boolean;
    breedingMethodSelectedId: string;
    breedingMethodSelected: BreedingMethod;
    femaleParent: string;
    maleParent: string;

    eventSubscriber: Subscription;
    selectorTarget: string;

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
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
        const maleParentsList = this.maleParent.split(',');

        if (this.isNumberOutOfRange([this.femaleParent])) {
            this.alertService.error('germplasm-progenitors-modal.validation.gid.out.of.range', { param: this.isMutation() ? 'Origin' : 'Female Parent or Group Source' });
            return;
        }

        if (!this.isMutation() && this.isNumberOutOfRange(maleParentsList)) {
            this.alertService.error('germplasm-progenitors-modal.validation.gid.out.of.range', { param: 'Male Parent or Immediate Source' });
            return;
        }

        if (!this.isMutation() && !this.allowMultipleMaleParents() && maleParentsList.length > 1) {
            this.alertService.error('germplasm-progenitors-modal.validation.only.one.male.parent.is.allowed', { param: this.breedingMethodSelected.name });
            return;
        }

        const maleParentsNumbers = maleParentsList.map((item) => Number(item));
        if (!this.isGenerative && this.progenitorsDetails.numberOfDerivativeProgeny > 0
            && (this.progenitorsDetails.breedingMethodType === BreedingMethodTypeEnum.GENERATIVE || this.hasProgenitorsChanges())) {
            const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
            confirmModalRef.componentInstance.title = 'Update Germplasm Progenitors';
            confirmModalRef.componentInstance.message = 'Germplasm has derivative progeny and the group source will change. ' +
                'Group source change will be applied to the progeny (' + this.progenitorsDetails.numberOfDerivativeProgeny + ' germplasm). Are you sure you want to continue?';
            confirmModalRef.result.then(() => {
                this.updateGermplasmProgenitors(maleParentsNumbers);
                this.activeModal.close();
            }, () => this.activeModal.dismiss());
        } else {
            this.updateGermplasmProgenitors(maleParentsNumbers);
        }
    }

    hasProgenitorsChanges() {
        // Compare the maleParentIds without splitting the string since only one germplasm is allowed as male parent for non-generative germplasm
        return this.getFemaleParentId(this.progenitorsDetails) !== this.femaleParent || this.getMaleParentId(this.progenitorsDetails) !== this.maleParent;
    }

    updateGermplasmProgenitors(maleParentsList) {
        this.isLoading = true;
        const firstMaleElement = maleParentsList.shift();
        this.germplasmService.updateGermplasmProgenitors(this.gid, {
            breedingMethodId: this.breedingMethodSelected.mid,
            gpid1: Number(this.femaleParent),
            gpid2: !this.isMutation() ? firstMaleElement : 0,
            otherProgenitors: !this.isMutation() ? maleParentsList : []
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
        this.breedingMethodSelectedId = String(this.progenitorsDetails.breedingMethodId);
        this.isGenerative = this.progenitorsDetails.breedingMethodType === BreedingMethodTypeEnum.GENERATIVE;
        this.femaleParent = this.getFemaleParentId(this.progenitorsDetails);
        this.maleParent = this.getMaleParentId(this.progenitorsDetails);
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'progenitorsChanged', content: '' });
        this.clear();
    }

    getFemaleParentId(progenitorsDetails: GermplasmProgenitorsDetails) {
        if (progenitorsDetails.femaleParent) {
            return String(this.progenitorsDetails.femaleParent.gid);
        } else if (progenitorsDetails.groupSource) {
            return String(this.progenitorsDetails.groupSource.gid);
        }
        // Return 0 if unknown
        return this.UNKNOWN;
    }

    getMaleParentId(progenitorsDetails: GermplasmProgenitorsDetails) {
        if (progenitorsDetails.maleParents && progenitorsDetails.maleParents.length > 0) {
            return this.progenitorsDetails.maleParents.map((parent) => parent.gid).join(',');
        } else if (progenitorsDetails.immediateSource) {
            return String(this.progenitorsDetails.immediateSource.gid);
        }
        // Return 0 if unknown
        return this.UNKNOWN;
    }

    isMutation(): boolean {
        return this.breedingMethodSelected && this.breedingMethodSelected.numberOfProgenitors === 1;
    }

    allowMultipleMaleParents(): boolean {
        return this.breedingMethodSelected && this.breedingMethodSelected.type === BreedingMethodTypeEnum.GENERATIVE && this.breedingMethodSelected.numberOfProgenitors === 0;
    }

    openGermplasmSelector(selectMultiple: boolean, target: string): void {
        this.selectorTarget = target;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-selector-dialog' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                cropName: this.paramContext.cropName,
                loggedInUserId: this.paramContext.loggedInUserId,
                programUUID: this.paramContext.programUUID,
                selectMultiple
            }
        });
    }

    isNumberOutOfRange(numbersString: string[]) {
        const maxInteger = 2147483647; // Maxiumum 32 bit integer;
        return numbersString.some((num) => Number.isNaN(Number.parseInt(num, 10)) || Number(num) > maxInteger);
    }

    onMethodChanged(selectedBreedingMethod: BreedingMethod): void {
        if (selectedBreedingMethod) {
            this.breedingMethodSelectedId = String(selectedBreedingMethod.mid);
            this.breedingMethodSelected = selectedBreedingMethod;
        }
    }

    breedingMethodOptionChanged(methodType: BreedingMethodFilterTypeEnum) {
        this.isGenerative = methodType === BreedingMethodFilterTypeEnum.GENERATIVE_ONLY;
        this.breedingMethodSelectedId = null;
        this.breedingMethodSelected = null;
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
