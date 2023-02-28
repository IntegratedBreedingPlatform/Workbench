import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';

import { Sample } from './sample.model';
import { SampleService } from './sample.service';
// import { ITEMS_PER_PAGE } from '../../shared';
import { ITEMS_PER_PAGE } from '../../shared';
import { SampleList } from './sample-list.model';
import { SampleListService } from './sample-list.service';
import { FileDownloadHelper } from './file-download.helper';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SampleImportPlateComponent } from './sample-import-plate.component';
import { ListBuilderContext } from '../../shared/list-builder/list-builder.context';
import { ListEntry } from '../../shared/list-builder/model/list.model';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';

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
    reverse: boolean;

    // { <data-index>: sample }
    selectedItems: Map<number, Sample> = new Map<number, Sample>();
    lastClickIndex: any;

    constructor(
        private sampleService: SampleService,
        private sampleListService: SampleListService,
        private languageservice: JhiLanguageService,
        // private parseLinks: JhiParseLinks,
        private alertService: AlertService,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager,
        private fileDownloadHelper: FileDownloadHelper,
        private modalService: NgbModal,
        public activeModal: NgbActiveModal,
        public listBuilderContext: ListBuilderContext,
        public translateService: TranslateService,
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.listBuilderContext.pageSize = this.itemsPerPage;

        this.routeData = this.activatedRoute.data.subscribe((data: any) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
        this.loadAll();

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
        this.router.navigate(['/sample-manager', {
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
        this.router.navigate(['/sample-manager', {
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

        });
    }
    importPlate() {
        const confirmModalRef = this.modalService.open(SampleImportPlateComponent as Component, { size: 'lg', backdrop: 'static' });
        confirmModalRef.result.then(() => {
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
    }
    removeEntries() {
        if (!this.validateSelection()) {
            return;
        }
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('bmsjHipsterApp.sample.delete-entries.confirm.message');
        confirmModalRef.componentInstance.title = this.translateService.instant('bmsjHipsterApp.sample.delete-entries.confirm.header');

        confirmModalRef.result.then(() => {
            this.sampleService.removeEntries(this.sampleList.id, this.getSelectedItemIds()).subscribe(() => {
                if (this.isPageSelected() && this.page === Math.ceil(this.totalItems / this.itemsPerPage)) {
                    this.page = 1;
                }
                this.selectedItems.clear();
                this.loadAll();
                this.alertService.success('bmsjHipsterApp.sample.delete-entries.delete.success');
            }, (error) => this.onError(error));
        }, () => confirmModalRef.dismiss());
    }

    ngOnInit() {
        this.loadAll();
        this.registerChangeInSamples();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
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

    toggleListBuilder() {
        this.listBuilderContext.visible = !this.listBuilderContext.visible;
    }

    isSelected(sample: Sample) {
        return this.selectedItems.get(sample.sampleId);
    }

    private getSelectedItemIds() {
        return Array.from(this.selectedItems.keys()).map((listDataId: any) => Number(listDataId));
    }

    toggleSelect($event, index, sample: Sample, checkbox = false) {
        if (!$event.ctrlKey && !checkbox) {
            this.selectedItems.clear();
        }
        let items;
        if ($event.shiftKey) {
            const max = Math.max(this.lastClickIndex, index) + 1,
                min = Math.min(this.lastClickIndex, index);
            items = this.sampleList.samples.slice(min, max);
        } else {
            items = [sample];
            this.lastClickIndex = index;
        }
        const isClickedItemSelected = this.selectedItems.get(sample.sampleId);
        for (const item of items) {
            if (isClickedItemSelected) {
                this.selectedItems.delete(item.sampleId);
            } else {
                this.selectedItems.set(item.sampleId, item);
            }
        }
    }

    isPageSelected() {
        return this.size() && this.sampleList.samples.every((s) => Boolean(this.selectedItems.get(s.sampleId)));
    }

    onSelectPage() {
        if (this.isPageSelected()) {
            // remove all items
            this.sampleList.samples.forEach((entry: Sample) => this.selectedItems.delete(entry.sampleId));
        } else {
            // check remaining items
            this.sampleList.samples.forEach((entry: Sample) => this.selectedItems.set(entry.sampleId, entry));
        }
    }

    size() {
        return this.selectedItems.size;
    }

    dragStart($event, dragged: Sample) {
        let selected;
        if (this.selectedItems.get(dragged.sampleId)) {
            // TODO sort as in table
            selected = Object.values(this.selectedItems).sort((a, b) => a.sampleId > b.sampleId ? 1 : -1);
        } else {
            selected = [dragged];
        }
        this.listBuilderContext.data = selected.map((sample) => {
            const row = new ListEntry();
            row['SAMPLE_ID'] = sample.sampleId;
            row['DESIGNATION'] = sample.designation;
            row['GID'] = sample.gid;
            row['SAMPLE_NAME'] = sample.sampleName;
            row['TAKEN_BY'] = sample.takenBy;
            row['SAMPLING_DATE'] = sample.samplingDate;
            row['SAMPLE_UID'] = sample.sampleBusinessKey;
            row['PLATE'] = sample.plateId;
            row['WELL'] = sample.well;
            return row;
        });
    }

    private validateSelection() {
        if (this.sampleList.samples.length === 0 || this.selectedItems.size === 0) {
            this.alertService.error('germplasm-list.list-data.selection.empty');
            return false;
        }
        return true;
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
        this.alertService.error(error.message);
    }
}
