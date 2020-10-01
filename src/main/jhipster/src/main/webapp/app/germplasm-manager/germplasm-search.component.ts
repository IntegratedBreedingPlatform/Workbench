import { Component, OnInit } from '@angular/core';
import { Germplasm } from '../entities/germplasm/germplasm.model';
import { GermplasmSearchRequest } from '../entities/germplasm/germplasm-search-request.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ITEMS_PER_PAGE } from '../shared';
import { ColumnFilterComponent } from '../shared/column-filter/column-filter.component';
import { GermplasmService } from '../shared/germplasm/service/germplasm.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-search',
    templateUrl: './germplasm-search.component.html',
    styleUrls: [] // TODO: Copy styles from Invetory Manager
})
export class GermplasmSearchComponent implements OnInit {

    germplasmList: Germplasm[];
    error: any;
    currentSearch: string;
    routeData: any;
    links: any;
    totalItems: any;
    queryCount: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;

    isLoading: boolean;

    germplasmSearchRequest = new GermplasmSearchRequest();
    germplasmFilters: any;
    germplasmHiddenColumns = {};
    resultSearch: any = {};

    get request() {
        return this.germplasmSearchRequest;
    }

    set request(request) {
        this.germplasmSearchRequest = request;
    }

    get filters() {
        return this.germplasmFilters;
    }

    set filters(filters) {
        this.germplasmFilters = filters;
    }

    get hiddenColumns() {
        return this.germplasmHiddenColumns;
    }

    set hiddenColumns(hiddenColumns) {
        this.germplasmHiddenColumns = hiddenColumns;
    }

    selectedItems: any[] = [];
    allItemsPerPages = false;

    // TODO: add filters
    private static getInitialFilters() {
        return [];
    }

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private germplasmService: GermplasmService,
                private router: Router,
                private jhiAlertService: JhiAlertService,
                private modal: NgbModal) {

        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.currentSearch = this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search'] ?
            this.activatedRoute.snapshot.params['search'] : '';

        if (!this.filters) {
            this.filters = GermplasmSearchComponent.getInitialFilters();
            ColumnFilterComponent.reloadFilters(this.filters, this.request);
        }
    }

    loadAll(request: GermplasmSearchRequest) {
        this.isLoading = true;
        this.germplasmService.searchGermplasm(request, {
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.sort()
            }
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<Germplasm[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );

    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

    transition() {
        this.router.navigate(['./'], {
            queryParams:
                {
                    page: this.page,
                    size: this.itemsPerPage,
                    search: this.currentSearch,
                    sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
                }, relativeTo: this.activatedRoute
        });
        this.loadAll(this.request);
    }

    clear() {
        this.page = 0;
        this.currentSearch = '';
        this.router.navigate(['./', {
            page: this.page,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        }], { relativeTo: this.activatedRoute });
        this.loadAll(this.request);
    }

    ngOnInit() {
        this.loadAll(this.request);
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        return result;
    }

    trackId(index: number, item: Germplasm) {
        return item.gid;
    }

    private onSuccess(data, headers) {
        // this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        // this.page = pagingParams.page;
        this.germplasmList = data;
    }

    private onError(response: HttpErrorResponse) {
        if (response.error && response.error.errors) {
            this.jhiAlertService.error('error.custom', {
                param: response.error.errors.map((err) => err.message).join('<br/>')
            }, null);
        } else {
            this.jhiAlertService.error(response.message, null, null);
        }
    }

    isSelected(germplasm: Germplasm) {
        return germplasm && this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid);
    }

    onSelectAllPage() {
        const allPageSelected = this.isAllPageSelected();
        const lotCurrentPage = this.germplasmList.map((germplasm) => germplasm.gid);
        if (allPageSelected) {
            this.selectedItems = this.selectedItems.filter((item) =>
                lotCurrentPage.indexOf(item) === -1);
        } else {
            this.selectedItems = lotCurrentPage.filter((item) =>
                this.selectedItems.indexOf(item) === -1
            ).concat(this.selectedItems);
        }
    }

    onSelectAllPages(selectAllItems) {
        this.allItemsPerPages = !selectAllItems;
        if (this.allItemsPerPages) {
            this.selectedItems = [];
        }
    }

    toggleSelect(germplasm: Germplasm) {
        if (this.selectedItems.length > 0 && this.selectedItems.find((item) => item === germplasm.gid)) {
            this.selectedItems = this.selectedItems.filter((item) => item !== germplasm.gid);
        } else {
            this.selectedItems.push(germplasm.gid);
        }
    }

    isAllPageSelected() {
        return this.germplasmList.length > 0 && !this.germplasmList.some((germplasm) => this.selectedItems.indexOf(germplasm.gid) === -1);
    }

    private validateSelection() {
        if (this.germplasmList.length === 0 || (!this.allItemsPerPages && this.selectedItems.length === 0)) {
            this.jhiAlertService.error('error.custom', {
                param: 'Please select at least one germplasm'
            }, null);
            return false;
        }
        return true;
    }

}