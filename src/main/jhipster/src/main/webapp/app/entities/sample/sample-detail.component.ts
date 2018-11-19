import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { Sample } from './sample.model';
import { SampleService } from './sample.service';

@Component({
    selector: 'jhi-sample-detail',
    templateUrl: './sample-detail.component.html'
})
export class SampleDetailComponent implements OnInit, OnDestroy {

    sample: Sample;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private sampleService: SampleService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSamples();
    }

    load(id) {
        this.sampleService.find(id)
            .subscribe((sampleResponse: HttpResponse<Sample>) => {
                this.sample = sampleResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSamples() {
        this.eventSubscriber = this.eventManager.subscribe(
            'sampleListModification',
            (response) => this.load(this.sample.id)
        );
    }
}
