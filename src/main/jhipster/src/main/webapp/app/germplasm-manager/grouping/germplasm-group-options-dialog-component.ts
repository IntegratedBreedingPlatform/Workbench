import { Component, Input } from '@angular/core';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmGroupingService } from '../../shared/germplasm/service/germplasm-grouping.service';

@Component({
    selector: 'jhi-germplasm-group-options-dialog',
    templateUrl: './germplasm-group-options-dialog.component.html',
})
export class GermplasmGroupOptionsDialogComponent {

    @Input()
    gids: number[];

    includeDescendants: boolean;
    preserveExistingGroup: boolean;
    isLoading: boolean;

    constructor(
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private germplasmGroupingService: GermplasmGroupingService
    ) {
    }

    group() {
        this.germplasmGroupingService.group({ gids: this.gids, includeDescendants: this.includeDescendants, preserveExistingGroup: this.preserveExistingGroup })
            .subscribe((response) => {
                this.modal.close(response);
            });
    }

    dismiss() {
        this.modal.dismiss();
    }

}
