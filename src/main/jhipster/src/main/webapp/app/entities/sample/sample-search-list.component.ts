import {AfterViewInit, Component} from '@angular/core';
import {SampleList} from './sample-list.model';
import {SampleListService} from './sample-list.service';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpResponse} from '@angular/common/http';

@Component({
    selector: 'jhi-sample-search-list',
    templateUrl: './sample-search-list.component.html',
    styleUrls: ['./sample-search-list.component.css']
})
export class SampleSearchListComponent {

    searchString: string;
    exactMatch = false;
    sampleListResults: SampleList[] = [];
    selectedListId = 0;
    displayHelpPopup = false;
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

        if (this.searchString.trim().length === 0) {
            this.sampleListResults = [];
            return;
        }

        this.sampleListService.search(this.searchString, this.exactMatch).subscribe(
            (res: HttpResponse<SampleList[]>) => {
                    this.sampleListResults = res.body;
                }
        )
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
        this.sampleListResults = [];
        this.selectedListId = 0;
    }

    hideHelpPopup() {
        this.displayHelpPopup = false;
    }
}
