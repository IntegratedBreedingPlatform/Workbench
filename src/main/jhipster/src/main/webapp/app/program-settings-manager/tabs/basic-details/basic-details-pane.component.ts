import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../shared/alert/alert.service';
import { ProgramService } from '../../../shared/program/service/program.service';
import { ParamContext } from '../../../shared/service/param.context';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { DateHelperService } from '../../../shared/service/date.helper.service';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { NavbarMessageEvent } from '../../../shared/model/navbar-message.event';
import { Program } from '../../../shared/program/model/program';

@Component({
    selector: 'jhi-basic-details-pane',
    templateUrl: 'basic-details-pane.component.html',
    styleUrls: ['basic-details-panel.component.css']
})
export class BasicDetailsPaneComponent implements OnInit, OnDestroy {

    isLoading: boolean;
    program: Program;
    programOrg: Program;
    startDate: NgbDate;

    constructor(private alertService: AlertService,
                private programService: ProgramService,
                private context: ParamContext,
                public dateHelperService: DateHelperService,
                private modalService: NgbModal,
                private translateService: TranslateService) {
        this.program = {};
    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
        this.programService.getProgramByProgramUUID(this.context.cropName, this.context.programUUID).subscribe(
            (res) => {
                this.program = res.body;
                this.startDate = this.dateHelperService.convertFormattedDateStringToNgbDate(this.program.startDate, 'yyyy-mm-dd');
                this.programOrg = Object.assign({}, this.program);
            }, (res: HttpErrorResponse) => {
                this.onError(res)
            }
        );
    }

    delete() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('program-settings-manager.basic-details.delete.question');
        confirmModalRef.componentInstance.title = this.translateService.instant('program-settings-manager.basic-details.delete.title');
        confirmModalRef.result.then(() => {
            this.programService.deleteProgram(this.context.cropName, this.context.programUUID).subscribe(
                () => {
                    const message: NavbarMessageEvent = { programDeleted: true };
                    window.parent.postMessage(message, '*');
                    this.alertService.success('program-settings-manager.basic-details.delete.success');
                }, (res: HttpErrorResponse) => {
                    this.onError(res)
                }, () => confirmModalRef.dismiss()
            );
        });
    }

    save() {
        this.isLoading = true;
        this.program.startDate = this.dateHelperService.convertFormattedNgbDateToString(this.startDate, '-');

        const programBasicDetails = {
            name: this.program.name,
            startDate: this.program.startDate
        };

        const message: NavbarMessageEvent = { programUpdated: this.program };
        this.programService.updateProgram(programBasicDetails, this.program.crop, this.program.uniqueID).subscribe(() => {
                this.programOrg = Object.assign({}, this.program);
                window.parent.postMessage(message, '*');
                this.alertService.success('program-settings-manager.basic-details.update.success');
            }, (res: HttpErrorResponse) => {
                this.onError(res)
            }, () => {
                this.isLoading = false;
            }
        );
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.program.name && this.program.crop
            && this.startDate;
    }

    reset() {
        this.program = Object.assign({}, this.programOrg);
        this.startDate = this.dateHelperService.convertFormattedDateStringToNgbDate(this.program.startDate, 'yyyy-mm-dd');
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
