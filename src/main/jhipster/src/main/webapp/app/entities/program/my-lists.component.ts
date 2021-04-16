import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MyListsService } from './my-lists.service';
import { map } from 'rxjs/operators';
import { MyList } from './my-list';

@Component({
    selector: 'jhi-my-lists',
    templateUrl: './my-lists.component.html'
})
export class MyListsComponent {
    programUUID: string;
    cropName: string;

    page = 1;
    pageSize = 10;
    totalCount: any;
    isLoading = false;

    lists: MyList[];

    constructor(
        private route: ActivatedRoute,
        private myListsService: MyListsService
    ) {
        this.route.queryParams.subscribe((params) => {
            this.cropName = params['cropName'];
            this.programUUID = params['programUUID'];
            this.load();
        });
    }

    load() {

        this.myListsService.getMyLists(
            this.page - 1,
            this.pageSize,
            this.cropName,
            this.programUUID
        ).pipe(map((resp) => {
            // TODO
            // this.totalCount = resp.headers.get('X-Total-Count')
            this.totalCount = 50;
            return resp.body;
        })).subscribe((lists) => {
            this.lists = lists;
        });
    }
}
