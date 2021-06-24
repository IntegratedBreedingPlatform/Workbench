import { Component, Input } from '@angular/core';
import { VariableDetails } from '../shared/ontology/model/variable-details';
import { VariableValidationStatusType, VariableValidationService } from '../shared/ontology/service/variable-validation.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-import-descriptors-confirmation-dialog',
    templateUrl: './germplasm-import-update-descriptors-confirmation-dialog.component.html'
})
export class GermplasmImportUpdateDescriptorsConfirmationDialogComponent {
    attributeStatusById: { [key: number]: VariableValidationStatusType; } = {};
    attributes: VariableDetails[] = [];

    constructor(
        private variableValidationService: VariableValidationService,
        private modal: NgbActiveModal
    ) {
    }

    getStatusIcon(attribute) {
        return this.variableValidationService.getStatusIcon(attribute, this.attributeStatusById)
    }

    dismiss() {
        this.modal.dismiss();
    }

    confirm() {
        this.modal.close();
    }
}
