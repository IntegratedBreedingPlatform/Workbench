import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmChangesService } from './germplasm-changes.service';
import { HttpResponse } from '@angular/common/http';
import { GermplasmNameChange } from './germplasm-name-changes.model';
import { RevisionType } from './revision-type';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmNameContext } from '../../entities/germplasm/name/germplasm-name.context';

@Component({
    selector: 'jhi-germplasm-name-changes-modal',
    templateUrl: './germplasm-name-changes-modal.component.html'
})
export class GermplasmNameChangesModalComponent implements OnInit {

    gid: number;
    nameId: number;
    title: string;

    germplasmNameChanges: GermplasmNameChange[];

    constructor(public activeModal: NgbActiveModal,
                private germplasmChangesService: GermplasmChangesService,
                private translateService: TranslateService,
                private germplasmNameContext: GermplasmNameContext) {
        this.title = this.translateService.instant('basic-details.audit-changes.title',
            { entity: 'Name', entityValue: this.germplasmNameContext.germplasmName.name });
    }

    ngOnInit(): void {
        this.germplasmChangesService.getNamesChanges(this.gid, this.nameId).subscribe((res: HttpResponse<GermplasmNameChange[]>) => {
            this.germplasmNameChanges = res.body;
        });
    }

    dismiss() {
        this.activeModal.dismiss('cancel');
    }

    getEventDate(germplasmNameChange: GermplasmNameChange): string {
        if (germplasmNameChange.revisionType ===  RevisionType.CREATION) {
            return germplasmNameChange.createdDate;
        }
        return germplasmNameChange.modifiedDate;
    }

    getUser(germplasmNameChange: GermplasmNameChange): string {
        if (germplasmNameChange.revisionType ===  RevisionType.CREATION) {
            return germplasmNameChange.createdBy;
        }
        return germplasmNameChange.modifiedBy;
    }
}

@Component({
    selector: 'jhi-germplasm-name-changes-popup',
    template: ``
})
export class GermplasmNameChangesPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmNameChangesModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
            modalRef.componentInstance.nameId = Number(this.route.snapshot.paramMap.get('nameId'));
        });

    }

}
