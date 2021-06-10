import { Component, OnInit } from '@angular/core';
import { GermplasmCodeNameSettingModel } from '../../shared/germplasm/model/germplasm-name-setting.model';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { JhiAlertService } from 'ng-jhipster';
import { GermplasmCodeNameType } from '../../shared/germplasm/model/germplasm-code-name-batch-request.model';

@Component({
    selector: 'jhi-germplasm-coding',
    templateUrl: './germplasm-coding-dialog.component.html'
})
export class GermplasmCodingDialogComponent {

    GermplasmCodeNameType = GermplasmCodeNameType;

    gids: number[];
    automaticNaming: boolean;
    nameType: GermplasmCodeNameType = GermplasmCodeNameType.CODE1;
    isProcessing: boolean;
    germplasmCodeNameSetting: GermplasmCodeNameSettingModel = new GermplasmCodeNameSettingModel();
    numberOfDigits = Array(9).fill(1).map((x, i) => i + 1);
    nextCodeNameSequence: string;

    constructor(private modal: NgbActiveModal,
                private germplasmService: GermplasmService,
                private alertService: JhiAlertService,
                private modalService: NgbModal) {
        this.automaticNaming = true;
        this.germplasmCodeNameSetting.addSpaceBetweenPrefixAndCode = false;
        this.germplasmCodeNameSetting.addSpaceBetweenSuffixAndCode = false;
        this.germplasmCodeNameSetting.numOfDigits = 1;
    }

    close() {
        this.modal.close();
    }

    applyCodes() {
        this.isProcessing = true;
        this.germplasmService.createGermplasmCodeNames({
            gids: this.gids,
            nameType: this.nameType,
            germplasmCodeNameSetting: this.automaticNaming ? null : this.germplasmCodeNameSetting
        }).toPromise().then((result) => {
            this.modal.close(result);
            this.isProcessing = false;
        }).catch((response) => {
            this.alertService.error('error.custom', { param: response.error.errors[0].message });
            this.isProcessing = false;
        });
    }

    validate() {
        return this.automaticNaming || (this.germplasmCodeNameSetting.prefix && this.germplasmCodeNameSetting.numOfDigits);
    }

    getNextNameSequence() {
        this.germplasmService.getNextGermplasmCodeNameInSequence(this.germplasmCodeNameSetting).subscribe((nextCodeNameSequence) => {
            this.nextCodeNameSequence = nextCodeNameSequence;
        });
    }

}
