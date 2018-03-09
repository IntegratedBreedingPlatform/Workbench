import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { SampleList } from './sample-list.model';
import { SampleListService } from './sample-list.service';

@Component({
    selector: 'jhi-sample-list-detail',
    templateUrl: './sample-list-detail.component.html'
})
export class SampleListDetailComponent implements OnInit, OnDestroy {

    sampleList: SampleList;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private sampleListService: SampleListService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSampleLists();
    }

    load(id) {
        this.sampleListService.find(id)
            .subscribe((sampleListResponse: HttpResponse<SampleList>) => {
                this.sampleList = sampleListResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSampleLists() {
        this.eventSubscriber = this.eventManager.subscribe(
            'sampleListListModification',
            (response) => this.load(this.sampleList.id)
        );
    }
}
