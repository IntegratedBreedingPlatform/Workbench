import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'jhi-study-summary',
    templateUrl: './study-summary.component.html',
})
export class StudySummaryComponent implements OnInit {

    @Input()
    studyId: number;

    constructor() {
    }

    ngOnInit(): void {
    }

}
