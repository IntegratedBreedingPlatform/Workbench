import { Component, Input, OnInit } from '@angular/core';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmGroupingService } from '../../shared/germplasm/service/germplasm-grouping.service';
import { GermplasmGroup, GermplasmGroupMember } from '../../shared/germplasm/model/germplasm-group.model';

@Component({
    selector: 'jhi-germplasm-grouping-result',
    templateUrl: './germplasm-grouping-result.component.html',
})
export class GermplasmGroupingResultComponent implements OnInit {

    @Input()
    results: GermplasmGroup[];

    isLoading: boolean;
    fixedGids: number[];

    MAX_NAME_DISPLAY_SIZE = 30;

    constructor(private alertService: AlertService, private modal: NgbActiveModal) {
    }

    ngOnInit() {
        const germplasmGrouped = this.results.find((g) => g.groupMembers.length > 0);
        if (germplasmGrouped) {
            this.alertService.success('germplasm-grouping.grouping.success');
        } else {
            this.alertService.warning('germplasm-grouping.grouping.warning');
        }
    }

    displayMembers(groupMembers: GermplasmGroupMember[]) {
        return groupMembers.map((member) => member.gid + '[' + member.preferredName + ']').join(',');
    }

    close() {
        this.modal.close();
    }

}
