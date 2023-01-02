import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { DatasetService } from '../shared/dataset/service/dataset.service';
import { DatasetModel } from '../shared/dataset/model/dataset.model';

@Component({
    selector: 'jhi-study-summary-dataset',
    templateUrl: './study-summary-dataset.component.html',
})
export class StudySummaryDatasetComponent implements OnInit {

    @Input()
    studyId: number;

    @Input()
    datasetId: number;

    dataset: DatasetModel;

    constructor(private alertService: AlertService,
                private translateService: TranslateService,
                private datasetService: DatasetService) {
    }

    ngOnInit(): void {
        this.datasetService.getDataset(this.studyId, this.datasetId).subscribe(
            (res: HttpResponse<DatasetModel>) => this.dataset = res.body,
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

}
