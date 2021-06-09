import { Component, OnInit } from '@angular/core';
import { GermplasmCodeNameSettingModel } from '../../shared/germplasm/model/germplasm-name-setting.model';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { JhiAlertService } from 'ng-jhipster';

@Component({
    selector: 'jhi-germplasm-coding',
    templateUrl: './germplasm-coding-dialog.component.html'
})
export class GermplasmCodingDialogComponent implements OnInit {

    gids: number[];
    automaticNaming: boolean;
    nameType: string = 'CODE1';
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

    ngOnInit(): void {
    }

    close() {
        this.modal.close();
    }

    applyCodes() {
        this.germplasmService.createGermplasmCodeNames({
            gids: this.gids,
            nameType: this.nameType,
            germplasmCodeNameSetting: this.automaticNaming ? null : this.germplasmCodeNameSetting
        }).subscribe((result) => {
            this.modal.close(result);
        });
    }

    getNextNameSequence() {
        this.germplasmService.getNextGermplasmCodeNameInSequence(this.germplasmCodeNameSetting).subscribe((nextCodeNameSequence) => {
            this.nextCodeNameSequence = nextCodeNameSequence;
        });
    }


}
