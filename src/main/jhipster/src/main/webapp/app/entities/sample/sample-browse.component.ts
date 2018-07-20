import { Component, OnDestroy, OnInit } from '@angular/core';
import { SampleList } from './sample-list.model';
import { Subscription } from 'rxjs/Subscription';
import { ActivatedRoute } from '@angular/router';
import {ModalService} from '../../shared/modal/modal.service';

@Component({
    selector: 'jhi-sample-browse',
    templateUrl: './sample-browse.component.html',
    styles: []
})
export class SampleBrowseComponent implements OnInit, OnDestroy {

    private listId: number;
    // private crop: string;
    private queryParamSubscription: Subscription;
    private paramSubscription: Subscription;

    lists: SampleList[] = [];

    constructor(private activatedRoute: ActivatedRoute,
                private modalService: ModalService) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = params['listId'];

            if (!this.listId) {
                return;
            }

            if (!this.exists(this.listId)) {
                this.lists.push(new SampleList(this.listId, 'List ' + this.listId, '', true, []));
            }

            this.setActive(this.listId);
        });
        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            // this.crop = params['crop'];
        });
    }

    closeTab(list: SampleList) {
        this.lists.splice(this.lists.indexOf(list), 1);
        if (list.active) {
            const first = this.lists[0];
            if (first) {
                first.active = true;
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
            }
        });
    }

    private exists(listId: number) {
        return this.lists.some((list) => list.id === listId);
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

}
