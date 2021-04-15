import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { BreedingMethod } from '../../../shared/breeding-method/model/breeding-method';
import { BreedingMethodService } from '../../../shared/breeding-method/service/breeding-method.service';
import { GermplasmProgenitorsContext } from './germplasm-progenitors.context';
import { GermplasmProgenitorsDetails } from '../../../shared/germplasm/model/germplasm.model';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';

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

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private breedingMethodService: BreedingMethodService,
                private germplasmProgenitorsContext: GermplasmProgenitorsContext,
                private germplasmService: GermplasmService,
                private alertService: JhiAlertService) {
        this.progenitorsDetails = this.germplasmProgenitorsContext.germplasmProgenitorsDetails;
    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
        this.loadBreedingMethods();
        this.initializeForm();
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.germplasmService.updateGermplasmProgenitors(this.gid, {
            breedingMethodId: this.breedingMethodSelected.mid,
            gpid1: null,
            gpid2: null,
            otherProgenitors: []
        }).toPromise().then((result) => {
            this.notifyChanges();
        }).catch((response) => {
            this.alertService.error('error.custom', { param: response.error.errors[0].message });
        });
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.breedingMethodSelected && this.femaleParent && this.maleParent;
    }

    initializeForm() {
        this.isGenerative = this.progenitorsDetails.breedingMethodType === 'GEN';
        this.breedingMethodSelected = {
            mid: this.progenitorsDetails.breedingMethodId
        };
        this.femaleParent = this.getFemaleParentId(this.progenitorsDetails);
        this.maleParent = this.getMaleParentId(this.progenitorsDetails);
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'progenitorsChanged' });
        this.clear();
    }

    getFemaleParentId(progenitorsDetails: GermplasmProgenitorsDetails) {
        if (progenitorsDetails.femaleParent) {
            return String(this.progenitorsDetails.femaleParent.gid);
        } else if (progenitorsDetails.groupSource) {
            return String(this.progenitorsDetails.groupSource.gid);
        }
        return '';
    }

    getMaleParentId(progenitorsDetails: GermplasmProgenitorsDetails) {
        if (progenitorsDetails.maleParents) {
            return this.progenitorsDetails.maleParents.map((parent) => parent.gid).join(',');
        } else if (progenitorsDetails.immediateSource) {
            return String(this.progenitorsDetails.immediateSource.gid);
        }
        return '';
    }

    loadBreedingMethods() {
        this.breedingMethodService.getBreedingMethods(false, ['GEN']).toPromise().then((result) => {
            this.generativeBreedingMethods = result;
        });
        this.breedingMethodService.getBreedingMethods(false, ['DER', 'MAN']).toPromise().then((result) => {
            this.derivativeBreedingMethods = result;
        });
    }

    /* Return true or false if it is the selected */
    compareById(idFist, idSecond): boolean {
        return idFist && idSecond && idFist.id == idSecond.id;
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
