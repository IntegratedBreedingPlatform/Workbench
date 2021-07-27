import { Component, OnInit } from '@angular/core';
import { NameTypeDetails } from '../../shared/germplasm/model/name-type.model';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { NameTypeService } from './name-type.service';
import { ParamContext } from '../../shared/service/param.context';
import { SORT_PREDICATE_NONE } from '../../germplasm-manager/germplasm-search-resolve-paging-params';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-name-type',
    templateUrl: './name-type.component.html'
})
export class NameTypeComponent implements OnInit {

    nameTypes: NameTypeDetails[];
    private routeData: any;
    itemsPerPage: any = 20;
    page: any;
    predicate: any;
    totalItems: number;
    private previousPage: any;
    reverse: any;
    isLoading: boolean;

    constructor(public translateService: TranslateService,
                private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private nameTypeService: NameTypeService,
                private alertService: AlertService,
                private router: Router,
                private modalService: NgbModal,
                private paramContext: ParamContext
    ) {

        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
    }

    ngOnInit(): void {
        this.loadAll();
    }

    loadAll() {
        this.nameTypeService.getNameTypes(this.addSortParam({
                page: this.page - 1,
                size: this.itemsPerPage
            })
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<NameTypeDetails[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.nameTypes = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    addSortParam(params) {
        const sort = this.predicate && this.predicate !== SORT_PREDICATE_NONE ? {
            sort: [this.getSort()]
        } : {};
        return Object.assign(params, sort);
    }

    getSort() {
        if (!this.predicate) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.sort();
        }
    }

    sort() {
        this.router.navigate(['./'], {
            queryParams:
                {
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
                },
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge'
        });
        this.loadAll();
    }

    openCreateNewTypeModal() {

    }

    editNameType(nameType: any) {

    }

    deleteNameType(nameType: any) {

    }

    async loadNameType() {
        this.loadAll();
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll();
    }

}
