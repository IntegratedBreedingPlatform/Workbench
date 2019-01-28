import { AfterViewInit, Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LabelsNeededSummary, LabelType, UserLabelPrinting } from './user-label-printing.model';
import { JhiLanguageService } from 'ng-jhipster';
import { printMockData } from './print-labels.mock-data';
import { PrintLabelsContext } from './print-labels.context';
import { PrintLabelsService } from './print-labels.service';
import { yyyymmdd } from '../shared/utils/dateutil';

declare const $: any;

@Component({
    selector: 'jhi-print-labels',
    templateUrl: './print-labels.component.html',
    styleUrls: ['./print-labels.component.css']
})
export class PrintLabelsComponent implements OnInit, AfterViewInit {
    userLabelPrinting: UserLabelPrinting = new UserLabelPrinting();
    printMockData = printMockData;
    labelsNeededSummary: LabelsNeededSummary;
    metadata: Map<string, string>;
    metadataKeys: string[];
    labelTypes: LabelType[];
    FILE_TYPES = FileType;
    fileType: FileType = FileType.NONE;

    constructor(private route: ActivatedRoute,
                private context: PrintLabelsContext,
                private service: PrintLabelsService,
                private languageService: JhiLanguageService) {
    }

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            this.context.datasetId = params['datasetId'];
            this.context.studyId = params['studyId'];
            this.context.printingLabelType = params['printingLabelType'];
        });
        this.service.getLabelsNeededSummary().subscribe((summary: any) => {
            this.labelsNeededSummary = summary;
        });
        this.service.getOriginResourceMetadada().subscribe((metadata) => {
            this.metadata = new Map(Object.entries(metadata));
            this.metadataKeys = Array.from(this.metadata.keys());
            this.userLabelPrinting.filename = 'Labels-for'
                + '-' + this.metadata.get('Name')
                + '-' + this.metadata.get('Selected dataset')
                + '-' + yyyymmdd();
        });
        this.service.getAvailableLabelFields().subscribe((labelTypes) => {
            this.labelTypes = labelTypes;
        });
    }

    ngAfterViewInit() {
        this.initDragAndDrop();
    }

    initDragAndDrop() {
        // TODO implement in angular
        setTimeout(() => {
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
        });
    }

    printLabels() {
        // TODO
        console.log($('#leftSelectedFields').sortable('toArray'))
        console.log($('#rightSelectedFields').sortable('toArray'))
    }

}

@Pipe({name: 'allLabels'})
export class AllLabelsPipe implements PipeTransform {
    transform(labelTypes: { fields: { id: number, name: string }[] }[]): any {
        if (!labelTypes) {
            return [];
        }
        return labelTypes
            .map((type) => type.fields)
            .reduce((allFields, fields) => allFields.concat(fields));
    }
}

export enum FileType {
    NONE = '',
    CSV = 'CSV',
    PDF = 'PDF',
    EXCEL = 'EXCEL'
}
