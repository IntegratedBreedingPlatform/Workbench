import {Component} from '@angular/core';
import {SampleList} from './sample-list.model';
import {SampleListService} from './sample-list.service';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'jhi-sample-search-list',
    templateUrl: './sample-search-list.component.html',
    styleUrls: ['./sample-search-list.component.css']
})
export class SampleSearchListComponent {

    searchString: string;
    exactMatch: boolean;
    totalResults: number = 0;
    sampleListResults: SampleList[] = [];
    selectedListId: number = 0;
    private paramSubscription: Subscription;
    private crop: string;

    constructor(private sampleListService: SampleListService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {

        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            this.crop = params['crop'];
            this.sampleListService.setCrop(this.crop);
        });

    }

    searchList() {
        this.sampleListResults = this.sampleListService.search('', true);
    }

    selectList(selectedSampleList: SampleList) {
        this.selectedListId = selectedSampleList.id;
        this.router.navigate(['/' + this.crop + '/sample-browse'], {queryParams: {
                listId: this.selectedListId
            }
        });
    }

    reset() {
        this.searchString = '';
        this.exactMatch = false;
        this.totalResults = 0;
        this.sampleListResults = [];
        this.selectedListId = 0;
    }

}
