import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserLabelPrinting } from './user-label-printing.model';
import { JhiLanguageService } from 'ng-jhipster';
import { printMockData } from './print-labels.mock-data';

@Component({
    selector: 'jhi-print-labels',
    templateUrl: './print-labels.component.html',
    styleUrls: ['./print-labels.component.css']
})
export class PrintLabelsComponent implements OnInit {
    datasetId: number;
    studyId: number;
    userLabelPrinting: UserLabelPrinting = {};
    printMockData = printMockData;

    constructor(private route: ActivatedRoute,
                private languageService: JhiLanguageService) {
    }

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            this.datasetId = params['datasetId'];
            this.studyId = params['studyId'];
        });
    }

}
