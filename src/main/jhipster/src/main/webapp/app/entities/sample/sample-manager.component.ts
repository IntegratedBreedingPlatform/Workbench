import { Component, OnDestroy, OnInit } from '@angular/core';
import { SampleList } from './sample-list.model';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalService } from '../../shared/modal/modal.service';
import { SampleContext } from './sample.context';
import { SampleListService } from './sample-list.service';
import { convertErrorResponse } from '../../shared';
import { JhiAlertService } from 'ng-jhipster';

declare const cropName: string;
declare var $: any;

@Component({
    selector: 'jhi-sample-manager',
    templateUrl: './sample-manager.component.html',
    styles: []
})
export class SampleManagerComponent implements OnInit, OnDestroy {

    private listId: number;
    private crop: string;
    private queryParamSubscription: Subscription;
    private paramSubscription: Subscription;

    lists: SampleList[] = [];

    constructor(private activatedRoute: ActivatedRoute,
                private modalService: ModalService,
                private router: Router,
                private sampleContext: SampleContext,
                private sampleListService: SampleListService,
                private jhiAlertService: JhiAlertService) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = params['listId'];

            if (!this.listId) {
                return;
            }

            if (!this.exists(this.listId)) {
                this.sampleListService.getById(this.listId).subscribe(
                    (resp) => {
                        this.lists.push(new SampleList(this.listId, resp.body.listName, '', true, resp.body.gobiiProjectId, []));
                    }, (resp) => convertErrorResponse(resp, this.jhiAlertService)
                )
            }

            this.setActive(this.listId);
        });
        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            this.crop = cropName;
        });
    }

    closeTab(list: SampleList) {
        this.lists.splice(this.lists.indexOf(list), 1);
        if (list.active) {
            const first = this.lists[0];
            if (first) {
                first.active = true;
                this.navigate(first.id);
            } else {
                this.navigate('');
            }
        }
    }

    openSearch() {
        this.modalService.open('search-sample-modal');
    }

    private setActive(listId: number) {
        this.lists.forEach((list) => {
            list.active = false;
            if (list.id === listId) {
                list.active = true;
                this.sampleContext.activeList = list;
            }
        });
    }

    private exists(listId: number) {
        return this.lists.some((list) => list.id === listId);
    }

    private navigate(listId: any) {
        this.listId = listId;
        this.router.navigate(['/sample-manager'], {queryParams: {
                listId: this.listId
            }
        });
    }

    trackId(index: number, item: SampleList) {
        return item.id;
    }

    ngOnInit() {
    }

    ngOnDestroy(): void {
        this.paramSubscription.unsubscribe();
        this.queryParamSubscription.unsubscribe();
    }

    browseList() {
        $('#listTreeModal').one('shown.bs.modal', () => {
            // FIXME tableStyleClass not working on primeng treetable 6?
            $('.ui-treetable-table').addClass('table table-curved table-condensed treetable');
        }).modal({ backdrop: 'static', keyboard: true });
    }
}
