import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'jhi-print-labels',
    templateUrl: './print-labels.component.html',
    styles: []
})
export class PrintLabelsComponent implements OnInit {
    private datasetId: number;
    private studyId: number;

    constructor(private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            this.datasetId = params['datasetId'];
            this.studyId = params['studyId'];
        });
    }

}
