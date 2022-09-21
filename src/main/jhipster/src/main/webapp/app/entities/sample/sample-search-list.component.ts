import { Component, OnDestroy, OnInit } from '@angular/core';
import { SampleList } from './sample-list.model';
import { SampleListService } from './sample-list.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiLanguageService } from 'ng-jhipster';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PopupService } from '../../shared/modal/popup.service';
import { ParamContext } from '../../shared/service/param.context';

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
    predicate = 'id';
    reverse: boolean;

    private paramSubscription: Subscription;

    constructor(private sampleListService: SampleListService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private alertService: AlertService,
                private languageservice: JhiLanguageService,
                public activeModal: NgbActiveModal,
                private modalService: NgbModal,
                private paramContext: ParamContext,
    ) {
    }

    searchList() {

        if (this.searchString.trim().length === 0) {
            this.sampleListResults = [];
            return;
        }

        const params = {
            searchString: this.searchString,
            exactMatch: this.exactMatch,
            programUUID : this.paramContext.programUUID,
            sort: this.sort()
        };
        this.sampleListService.search(params).subscribe(
            (res: HttpResponse<SampleList[]>) => { this.sampleListResults = res.body; } ,
            (res: HttpErrorResponse) => this.alertService.error(res.message)
        );
    }

    selectList(selectedSampleList: SampleList) {
        this.selectedListId = selectedSampleList.id;
        this.router.navigate(['/sample-manager'], {queryParams: {
                listId: this.selectedListId
            }
        });
    }

    reset() {
        this.searchString = '';
        this.exactMatch = false;
        this.sampleListResults = [];
        this.selectedListId = 0;
        this.dismiss();
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

    confirm() {
        this.activeModal.close('confirm');
    }

    dismiss() {
        this.activeModal.dismiss('cancel');
    }
}
