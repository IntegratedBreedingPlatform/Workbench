import { Component, OnInit } from '@angular/core';
import { toUpper } from '../../shared/util/to-upper';
import { HEADERS } from './germplasm-list-import.component';
import { Router } from '@angular/router';
import { ParamContext } from '../../shared/service/param.context';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-list-import-manual-matches',
    templateUrl: 'germplasm-list-import-manual-matches.component.html'
})
export class GermplasmListImportManualMatchesComponent implements OnInit {

    HEADERS = HEADERS;

    page = 0;
    pageSize = 10;
    isLoading: boolean;

    dataRow: any = {};

    // table displayed in html
    rows: any[] = [];

    selectMatchesResult: { [key: string]: number };
    selectedRow;

    eventSubscriber: Subscription;

    constructor(private eventManager: JhiEventManager,
                private router: Router,
                private germplasmService: GermplasmService,
                private modal: NgbActiveModal,
                private paramContext: ParamContext) {
    }

    ngOnInit(): void {
        this.registerGermplasmSelectorSelected();
    }

    keys(obj) {
        return Object.keys(obj);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    toUpper(param) {
        return toUpper(param);
    }

    back() {
        this.modal.dismiss();
    }

    next() {
        this.modal.close(this.selectMatchesResult);
        return;
    }

    dismiss() {
        this.modal.dismiss();
    }

    selectGermplasm(row) {
        this.selectedRow = row;
        const selectMultiple = false;

        this.router.navigate(['/', { outlets: { popup: 'germplasm-selector-dialog' } }], {
            queryParamsHandling: 'merge',
            queryParams: {
                cropName: this.paramContext.cropName,
                loggedInUserId: this.paramContext.loggedInUserId,
                programUUID: this.paramContext.programUUID,
                authToken: this.paramContext.authToken,
                selectMultiple
            }
        });
    }

    registerGermplasmSelectorSelected() {
        this.eventSubscriber = this.eventManager.subscribe('germplasmSelectorSelected', (event) => {
            this.germplasmService.getGermplasmMatches(undefined, undefined, [event.content], undefined).pipe(
                finalize(() => this.isLoading = false)
            ).subscribe((germplasm) => {
                this.selectedRow['MAPPED GID'] = germplasm[0].gid;
                this.selectedRow['MAPPED DESIGNATION'] = germplasm[0].preferredName;

                const entry = this.selectedRow['ENTRY_NO'];
                this.selectMatchesResult[entry] = germplasm[0].gid;

            });

        });
    }

    validMapped(row) {
        return (row['MAPPED GID'] ? true : false);
    }
}
