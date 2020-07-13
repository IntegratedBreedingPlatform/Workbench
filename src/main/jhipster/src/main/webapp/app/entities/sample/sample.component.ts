import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';

import { Sample } from './sample.model';
import { SampleService } from './sample.service';
import { convertErrorResponse, ITEMS_PER_PAGE } from '../../shared';
import { SampleList } from './sample-list.model';
import { SampleListService } from './sample-list.service';
import { FileDownloadHelper } from './file-download.helper';
import { ModalService } from '../../shared/modal/modal.service';
import { GobiiService } from './gobii.service';

declare const cropName: string;
declare const currentProgramId: string;

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
    gobiiSubmissionStatus: boolean;

    constructor(
        private sampleService: SampleService,
        private sampleListService: SampleListService,
        private languageservice: JhiLanguageService,
        // private parseLinks: JhiParseLinks,
        private jhiAlertService: JhiAlertService,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager,
        private fileDownloadHelper: FileDownloadHelper,
        private modalService: ModalService,
        private gobiiService: GobiiService
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;

        this.routeData = this.activatedRoute.data.subscribe((data: any) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            this.crop = cropName;
            this.sampleService.setCropAndProgram(this.crop, currentProgramId);
            this.loadAll();
        });

        this.currentSearch = this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search'] ?
            this.activatedRoute.snapshot.params['search'] : '';
        this.gobiiService.getGobiiSubmissionStatus().subscribe((resp) => {
            this.gobiiSubmissionStatus = resp;
        })

    }

    loadAll() {
        if (!this.sampleList || !this.sampleList.id) {
            return;
        }

        this.sampleService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            listId: this.sampleList.id,
            sort: this.sort()}).subscribe(
                (res: HttpResponse<Sample[]>) => this.onSuccess(res.body, res.headers),
                (res: HttpErrorResponse) => convertErrorResponse(res, this.jhiAlertService)
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

    export() {
        this.sampleListService.download(this.sampleList.id, this.sampleList.listName).subscribe((response) => {

            const fileName = this.fileDownloadHelper.getFileNameFromResponseContentDisposition(response);
            this.fileDownloadHelper.save(response.body, fileName);

        })
    }

    importPlate() {
        this.modalService.open('import-plate-modal');
    }

    submitToGOBii() {
        this.modalService.open('gobii-submission-modal');
    }

    ngOnInit() {
        this.loadAll();
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
    }
}
