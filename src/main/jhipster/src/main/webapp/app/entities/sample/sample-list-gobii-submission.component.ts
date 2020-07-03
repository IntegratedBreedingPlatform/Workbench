import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ModalService } from '../../shared/modal/modal.service';
import { SampleContext } from './sample.context';
import { JhiAlertService } from 'ng-jhipster';
import { SampleListService } from './sample-list.service';
import { GobiiContactService } from './gobii-contact.service';
import { convertErrorResponse } from '../../shared';
import { GobiiContact } from './gobii-contact.model';

@Component({
    selector: 'jhi-sample-list-gobii-submission',
    templateUrl: './sample-list-gobii-submission.component.html'
})

export class SampleListGobiiSubmissionComponent {

    modalId = 'gobii-submission-modal';

    gobiiContacts: Promise<GobiiContact[]>;

    @Input() gobiiContactId: number;

    @Output() onClose = new EventEmitter();

    constructor(private modalService: ModalService,
                private alertService: JhiAlertService,
                private sampleContext: SampleContext,
                private sampleListService: SampleListService,
                private gobiiContactService: GobiiContactService) {
        this.gobiiContacts = this.gobiiContactService.getAll().toPromise();
    }

    submit() {
        const activeList = this.sampleContext.activeList;

        this.sampleListService.submitToGOBii(activeList.id, this.gobiiContactId).subscribe((resp: number) => {
            activeList.gobiiProjectId = resp;
            this.close();
            this.alertService.success('bmsjHipsterApp.sample.gobii.submit.success', { sampleList: activeList.listName });
        }, (response) => {
            convertErrorResponse(response, this.alertService)
        });
    }

    reset() {
        this.gobiiContactId = null;
    }

    close() {
        this.modalService.close(this.modalId);
        this.reset();
        this.onClose.emit();
    }

}
