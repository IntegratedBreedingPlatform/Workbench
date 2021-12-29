import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { CropService } from '../../shared/crop/service/crop.service';
import { JhiLanguageService } from 'ng-jhipster';
import { ProgramService } from '../../shared/program/service/program.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { Program } from '../../shared/program/model/program';
import { NgbActiveModal, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { NavbarMessageEvent } from '../../shared/model/navbar-message.event';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { DateHelperService } from '../../shared/service/date.helper.service';

@Component({
    selector: 'jhi-create-program',
    templateUrl: 'create-program-dialog.component.html',
    styleUrls: ['create-program-dialog.component.css']
})
export class CreateProgramDialogComponent implements OnInit, OnDestroy {

    crops: string[];
    cropName: string;
    cropChanged = new Subject<string>();
    program: Program = new Program();
    startDate: NgbDate;
    isLoading: boolean;

    constructor(private alertService: AlertService,
                private activeModal: NgbActiveModal,
                private cropService: CropService,
                private programService: ProgramService,
                public dateHelperService: DateHelperService,
                private languageService: JhiLanguageService
    ) {
    }

    ngOnDestroy(): void {
    }

    async ngOnInit() {
        this.crops = await this.cropService.getCrops().toPromise();

    }

    onCropChange() {
    }

    isFormValid(f) {
        const form = f.form;
        if (!Object.values(form.controls).filter((control: any) => !control.disabled).length) {
            return false;
        }
        return f.form.valid && this.cropName;
    }

    create(f) {
        if (this.isFormValid(f)) {
            this.isLoading = true;
            const programBasicDetails = {};
            programBasicDetails['name'] = this.program.name;
            this.program.startDate = this.dateHelperService.convertFormattedNgbDateToString(this.startDate, '-');
            programBasicDetails['startDate'] = this.program.startDate;
            this.programService.addProgram(programBasicDetails, this.cropName).subscribe((program) => {
                    const message: NavbarMessageEvent = { programSelected: program, toolSelected: '/ibpworkbench/controller/jhipster#program-settings-manager' };
                    window.parent.postMessage(message, '*');
                    this.alertService.success('program.create.success');
                }, (res: HttpErrorResponse) => {
                    this.onError(res)
                }, () => {
                    this.isLoading = false;
                    this.activeModal.dismiss('cancel')
                }
            );
        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }
}

@Component({
    selector: 'jhi-create-program-popup',
    template: ''
})
export class CreateProgramPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private popupService: PopupService
    ) {

    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe(() => {
            this.popupService.open(CreateProgramDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
