import { Component, OnInit } from '@angular/core';
import { GermplasmNameSettingModel } from '../../shared/germplasm/model/germplasm-name-setting.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
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
    germplasmNameSetting: GermplasmNameSettingModel = new GermplasmNameSettingModel();
    numberOfDigits = Array(9).fill(1).map((x, i) => i + 1);
    nextNameSequence: string;

    constructor(private modal: NgbActiveModal, private germplasmService: GermplasmService, private alertService: JhiAlertService) {
        this.automaticNaming = true;
        this.germplasmNameSetting.addSpaceBetweenPrefixAndCode = false;
        this.germplasmNameSetting.addSpaceBetweenSuffixAndCode = false;
        this.germplasmNameSetting.numOfDigits = 1;
    }

    ngOnInit(): void {
    }

    close() {
        this.modal.close();
    }

    applyCodes() {
        this.germplasmService.createGermplasmNames({
            gids: this.gids,
            nameType: this.nameType,
            germplasmNameSetting: this.automaticNaming ? null : this.germplasmNameSetting
        }).subscribe((result) => {
            this.alertService.success('germplasm-coding.success');
        });
    }

    getNextNameSequence() {
        this.germplasmService.getNextNameInSequence(this.germplasmNameSetting).subscribe((nextNameSequence) => {
            this.nextNameSequence = nextNameSequence;
        });
    }


}
