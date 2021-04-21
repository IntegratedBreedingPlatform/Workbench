import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MyListsService } from './my-lists.service';
import { finalize, map } from 'rxjs/operators';
import { MyList } from './my-list';
import { Pageable } from '../../shared/model/pageable';
import { UrlService } from '../../shared/service/url.service';
import { ProgramContext } from './program.context';

@Component({
    selector: 'jhi-my-lists',
    templateUrl: './my-lists.component.html'
})
export class MyListsComponent {

    page = 1;
    pageSize = 5;
    predicate = 'date';
    reverse = false;
    totalCount: any;
    isLoading = false;

    lists: MyList[];

    constructor(
        private route: ActivatedRoute,
        private myListsService: MyListsService,
        public urlService: UrlService,
        public context: ProgramContext
    ) {
        this.route.queryParams.subscribe((params) => {
            // params['programUUID'] just to listen for changes
            this.load();
        });
    }

    load() {
        this.isLoading = true;
        this.myListsService.getMyLists(
            <Pageable>({
                page: this.page - 1,
                size: this.pageSize,
                sort: this.getSort()
            }),
            this.context.program.crop,
            this.context.program.uniqueID
        ).pipe(map((resp) => {
            this.totalCount = resp.headers.get('X-Total-Count')
            return resp.body;
        })).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((lists) => {
            this.lists = lists;
        });
    }

    sort() {
        this.page = 1;
        this.load();
    }

    getSort() {
        if (!this.predicate) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }
}
