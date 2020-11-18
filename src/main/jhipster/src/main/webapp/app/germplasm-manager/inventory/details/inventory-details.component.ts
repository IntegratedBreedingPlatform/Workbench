import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Germplasm } from '../../../entities/germplasm/germplasm.model';

@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-inventory-manager',
    templateUrl: './inventory-details.component.html',
    styleUrls: ['../../../../content/css/global-bs4.scss']
})
export class InventoryDetailsComponent implements OnInit {

    private gid: number;

    constructor(private activeModal: NgbActiveModal,
                private route: ActivatedRoute,
                private germplasmService: GermplasmService
    ) {
        this.route.queryParams.subscribe((value) => {
            this.gid = value.gid;
        });
    }

    ngOnInit() {
        this.germplasmService.searchGermplasm(request,
            this.addSortParam({
                page: this.page - 1,
                size: this.itemsPerPage
            })
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<Germplasm[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    cancel() {
        console.log('closeeeeeeeeee');
        this.activeModal.dismiss();
        (<any>window.parent).closeModal();
    }

}
