import { Component, Input } from '@angular/core';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { KeySequenceRegisterService } from '../../shared/key-sequence-register/service/key-sequence-register.service';

@Component({
    selector: 'jhi-key-sequence-register-deletion-dialog',
    templateUrl: './key-sequence-register-deletion-dialog.component.html',
})
export class KeySequenceRegisterDeletionDialogComponent {

    @Input()
    gids: number[];

    prefixValue: string;
    isLoading: boolean;
    prefixes: Array<string> = [];

    constructor(
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private keySequenceRegisterService: KeySequenceRegisterService
    ) {
    }

    delete() {

        this.keySequenceRegisterService.deleteKeySequencePrefixes(this.gids, this.prefixes).subscribe((response) => {
            if (response.deletedPrefixes.length === this.prefixes.length) {
                // All specified prefixes are deleted.
                this.alertService.success('key-sequence-register.prefix.delete.success');
            } else if (response.deletedPrefixes && response.deletedPrefixes.length === 0) {
                // No names found that match the prefixes specified.
                this.alertService.warning('key-sequence-register.no.existing.name.with.prefix');
            } else {
                // Some prefixes are deleted, and some are not found.
                this.alertService.warning('key-sequence-register.prefix.delete.warning',
                    { param1: response.deletedPrefixes.join(','), param2: response.undeletedPrefixes.join(',') });
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
