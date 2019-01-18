import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserLabelPrinting } from './user-label-printing.model';
import { JhiLanguageService } from 'ng-jhipster';
import { printMockData } from './print-labels.mock-data';

declare const $: any;

@Component({
    selector: 'jhi-print-labels',
    templateUrl: './print-labels.component.html',
    styleUrls: ['./print-labels.component.css']
})
export class PrintLabelsComponent implements OnInit, AfterViewInit {
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

    ngAfterViewInit() {
        // TODO implement in angular
        $('ul.droppable').sortable({
            connectWith: 'ul',
            receive: (event, ui) => {
                // event.currentTarget was not working
                const receiver = $(event.target),
                    sender = $(ui.sender),
                    item = $(ui.item);

                if (!receiver.hasClass('print-fields')
                    && item.attr('data-label-type-key') !== receiver.attr('data-label-type-key')) {

                    $(ui.sender).sortable('cancel');
                }
            }
        });
    }

    printLabels() {
        // TODO
        console.log($('#leftSelectedFields').sortable('toArray'))
        console.log($('#rightSelectedFields').sortable('toArray'))
    }

}
