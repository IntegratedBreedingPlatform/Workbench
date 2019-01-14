import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { JhiLanguageService } from 'ng-jhipster';

import { Sample } from './sample.model';
import { SampleService } from './sample.service';
// import { ITEMS_PER_PAGE } from '../../shared';
import { Principal, ITEMS_PER_PAGE } from '../../shared';
import { SampleList } from './sample-list.model';
import { SampleListService } from './sample-list.service';
import { FileDownloadHelper } from './file-download.helper';
import {ModalService} from '../../shared/modal/modal.service';

declare const cropName: string;

@Component({
    selector: 'jhi-sample',
    templateUrl: './sample.component.html'
})
export class SampleComponent implements OnInit, OnDestroy {

    @Input()
    sampleList: SampleList;

    currentAccount: any;
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
    crop: string;
    private paramSubscription: Subscription;

    constructor(
        private sampleService: SampleService,
        private sampleListService: SampleListService,
        private languageservice: JhiLanguageService,
        // private parseLinks: JhiParseLinks,
        private jhiAlertService: JhiAlertService,
        private principal: Principal,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager,
        private fileDownloadHelper: FileDownloadHelper,
        private modalService: ModalService
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;

        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            this.crop = cropName;
            this.sampleService.setCrop(this.crop);
            this.loadAll();
        });

        this.currentSearch = this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search'] ?
            this.activatedRoute.snapshot.params['search'] : '';
    }

    loadAll() {
        if (!this.sampleList || !this.sampleList.id) {
            return;
        }
        // TODO jhipster elastic search
        /*
        if (this.currentSearch) {
            this.sampleService.search({
                page: this.page - 1,
                query: this.currentSearch,
                size: this.itemsPerPage,
                listId: this.sampleList.id,
                sort: this.sort()}).subscribe(
                    (res: HttpResponse<Sample[]>) => this.onSuccess(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        */
        this.sampleService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            listId: this.sampleList.id,
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
        this.loadAll();
    }

    clear() {
        this.page = 0;
        this.currentSearch = '';
        this.router.navigate(['/sample-browse', {
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
        this.router.navigate(['/sample-browse', {
            search: this.currentSearch,
            page: this.page,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        }]);
        this.loadAll();
    }
    export() {
        this.sampleListService.download(this.sampleList.id, this.sampleList.listName).subscribe((response) => {

            const fileName = this.fileDownloadHelper.getFileNameFromResponseContentDisposition(response);
            this.fileDownloadHelper.save(response.body, fileName);

        })
    }
    importPlate() {
        this.modalService.open('import-plate-modal');
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
        // TODO
        // this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;

        this.sampleList.samples = data;
        if (data.length) {
            this.sampleList.listName = data[0].sampleList;
        }
    }
    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
