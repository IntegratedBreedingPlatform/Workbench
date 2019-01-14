import {Component} from '@angular/core';
import {SampleList} from './sample-list.model';
import {SampleListService} from './sample-list.service';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {JhiAlertService, JhiLanguageService} from 'ng-jhipster';

declare const cropName: string;

@Component({
    selector: 'jhi-sample-search-list',
    templateUrl: './sample-search-list.component.html',
    styleUrls: ['./sample-search-list.component.css']
})
export class SampleSearchListComponent {

    modalId = 'search-sample-modal';
    searchString: string;
    exactMatch = false;
    sampleListResults: SampleList[] = [];
    selectedListId = 0;
    displayHelpPopup = false;
    predicate: any;
    reverse: any;

    private paramSubscription: Subscription;
    private crop: string;

    constructor(private sampleListService: SampleListService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private jhiAlertService: JhiAlertService,
                private languageservice: JhiLanguageService) {

        this.paramSubscription = this.activatedRoute.params.subscribe((params) => {
            this.crop = cropName;
            this.sampleListService.setCrop(this.crop);
        });
        this.activatedRoute.data.subscribe((data) => {
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });

    }

    searchList() {

        if (this.searchString.trim().length === 0) {
            this.sampleListResults = [];
            return;
        }

        const params = {
            searchString: this.searchString,
            exactMatch: this.exactMatch,
            sort: this.sort()
        }
        this.sampleListService.search(params).subscribe(
            (res: HttpResponse<SampleList[]>) => { this.sampleListResults = res.body; } ,
            (res: HttpErrorResponse) => this.jhiAlertService.error(res.message, null, null)
        )
    }

    selectList(selectedSampleList: SampleList) {
        this.selectedListId = selectedSampleList.id;
        this.router.navigate(['/sample-browse'], {queryParams: {
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

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    trackId(index: number, item: SampleList) {
        return item.id;
    }

    setCrop(crop: string) {
        this.crop = crop;
    }
}
