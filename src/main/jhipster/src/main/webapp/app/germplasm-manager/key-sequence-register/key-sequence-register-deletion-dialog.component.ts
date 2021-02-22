import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { KeySequenceRegisterService } from '../../shared/key-sequence-register/service/key-sequence-register.service';

@Component({
    selector: 'jhi-key-sequence-register-deletion-dialog',
    templateUrl: './key-sequence-register-deletion-dialog.component.html',
})
export class KeySequenceRegisterDeletionDialogComponent implements OnInit {

    @Input()
    gids: number[];

    prefixValue: string;
    isLoading: boolean;
    prefixes: Array<string> = [];

    constructor(
        private translateService: TranslateService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private keySequenceRegisterService: KeySequenceRegisterService
    ) {
    }

    ngOnInit(): void {
    }

    delete() {

        this.keySequenceRegisterService.deleteKeySequencePrefixes(this.gids, this.prefixes).subscribe((response) => {
            if (response.deletedPrefixes.length === this.prefixes.length) {
                this.alertService.success('key-sequence-register.prefix.delete.success');
            } else if (response.deletedPrefixes && response.deletedPrefixes.length === 0) {
                this.alertService.warning('key-sequence-register.no.existing.name.with.prefix');
            } else {
                this.alertService.warning('key-sequence-register.prefix.delete.warning');
            }
            this.dismiss();
        });

    }

    dismiss() {
        this.modal.dismiss();
    }

    add(prefix: string) {
        if (prefix && this.prefixes.indexOf(prefix) === -1) {
            this.prefixes.push(prefix);
            this.prefixValue = '';
        }
    }

    remove(index: number) {
        this.prefixes.splice(index, 1);
    }
}
