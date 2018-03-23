import { Component, OnInit, OnDestroy } from '@angular/core';
import { SampleList } from "./sample-list.model";
import { Subscription } from 'rxjs/Subscription';
import { ActivatedRoute, Router } from '@angular/router';
import { Sample } from "./sample.model";

@Component({
    selector: 'jhi-sample-browse',
    templateUrl: './sample-browse.component.html',
    styles: []
})
export class SampleBrowseComponent implements OnInit {

    private listId: number;
    private queryParamSubscription: Subscription;

    lists: SampleList[] = new Array<SampleList>();
    listIds: number[] = new Array<number>();

    constructor(private activatedRoute: ActivatedRoute) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = params["listId"];
            if (!this.listId) {
                return;
            }

            if (!this.exists(this.listId)) {
                this.lists.push(new SampleList(this.listId, "List " + this.listId, true, new Array<Sample>()));
            }

            this.setActive(this.listId);
        });
    }

    private setActive(listId: number) {
        this.lists.forEach((list) => {
            list.active = false;
            if (list.id == listId) {
                list.active = true;
            }
        });
    }

    private exists(listId: number) {
        return this.lists.some(list => list.id == this.listId);
    }

    trackId(index: number, item: SampleList) {
        return item.id;
    }

    ngOnInit() {
    }

}
