import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';
import { JhiLanguageService } from 'ng-jhipster';

import { Sample } from './sample.model';
import { SampleService } from './sample.service';
import { ITEMS_PER_PAGE, Principal } from '../../shared';

@Component({
    selector: 'jhi-sample',
    templateUrl: './sample.component.html'
})
export class SampleComponent implements OnInit, OnDestroy {

currentAccount: any;
    samples: Sample[];
    error: any;
    success: any;
    eventSubscriber: Subscription;
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
    listId: number = 1;
    crop: string;
    private queryParamSubscription: Subscription;
    private paramSubscription: Subscription;

    constructor(
        private sampleService: SampleService,
        private languageService: JhiLanguageService,
        private parseLinks: JhiParseLinks,
        private jhiAlertService: JhiAlertService,
        private principal: Principal,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            this.crop = params['crop'];
            this.sampleService.setCrop(this.crop);
            this.loadAll();
        });
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = params["listId"];
            this.loadAll();
        });

        this.currentSearch = this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search'] ?
            this.activatedRoute.snapshot.params['search'] : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.sampleService.search({
                page: this.page - 1,
                query: this.currentSearch,
                size: this.itemsPerPage,
                listId: this.listId,
                sort: this.sort()}).subscribe(
                    (res: HttpResponse<Sample[]>) => this.onSuccess(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.sampleService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            listId: this.listId,
            sort: this.sort()}).subscribe(
                (res: HttpResponse<Sample[]>) => this.onSuccess(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }
    transition() {
        this.router.navigate(['/sample'], {queryParams:
            {
                page: this.page,
                size: this.itemsPerPage,
                search: this.currentSearch,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc'),
                listId: this.listId
            }
        });
        this.loadAll();
    }

    clear() {
        this.page = 0;
        this.currentSearch = '';
        this.router.navigate(['/sample', {
            page: this.page,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        }]);
        this.loadAll();
    }
    search(query) {
        if (!query) {
            return this.clear();
        }
        this.page = 0;
        this.currentSearch = query;
        this.router.navigate(['/sample', {
            search: this.currentSearch,
            page: this.page,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        }]);
        this.loadAll();
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInSamples();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
        this.queryParamSubscription.unsubscribe();
        this.paramSubscription.unsubscribe();
    }

    trackId(index: number, item: Sample) {
        return item.id;
    }
    registerChangeInSamples() {
        this.eventSubscriber = this.eventManager.subscribe('sampleListModification', (response) => this.loadAll());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private onSuccess(data, headers) {
        // FIXME
        // this.links = this.parseLinks.parse(headers.get('link'));
        // this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        // this.page = pagingParams.page;
        this.samples = data;
    }
    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
