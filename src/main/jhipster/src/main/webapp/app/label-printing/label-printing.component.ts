import { AfterViewInit, Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LabelsNeededSummary, LabelType, LabelPrintingData } from './label-printing.model';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { LabelPrintingContext } from './label-printing.context';
import { LabelPrintingService } from './label-printing.service';
import { FileDownloadHelper } from '../entities/sample/file-download.helper';
import { HttpErrorResponse } from '@angular/common/http';

declare const $: any;

@Component({
    selector: 'jhi-label-printing',
    templateUrl: './label-printing.component.html',
    styleUrls: ['./label-printing.component.css']
})
export class LabelPrintingComponent implements OnInit, AfterViewInit {
    labelPrintingData: LabelPrintingData = new LabelPrintingData();
    labelsNeededSummary: LabelsNeededSummary;
    metadata: Map<string, string>;
    metadataKeys: string[];
    labelTypes: LabelType[];
    FILE_TYPES = FileType;
    fileType: FileType = FileType.NONE;

    constructor(private route: ActivatedRoute,
                private context: LabelPrintingContext,
                private service: LabelPrintingService,
                private languageService: JhiLanguageService,
                private fileDownloadHelper: FileDownloadHelper,
                private alertService: JhiAlertService) {
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
        this.service.getOriginResourceMetadada().subscribe((originResourceMetadata) => {
            this.metadata = new Map(Object.entries(originResourceMetadata.metadata));
            this.metadataKeys = Array.from(this.metadata.keys());
            this.labelPrintingData.filename = originResourceMetadata.defaultFileName;
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
        const fieldsSelected = [];
        const barcodeFieldsSelected = [];

        if (this.fileType === FileType.CSV) {
            fieldsSelected.push($('#leftSelectedFields').sortable('toArray'));
        } else {
            fieldsSelected.push($('#leftSelectedFields').sortable('toArray'));
            fieldsSelected.push($('#rightSelectedFields').sortable('toArray'));
        }

        if (this.labelPrintingData.barcodeNeeded && !this.labelPrintingData.barcodeGeneratedAutomatically) {
            if (this.labelPrintingData.firstBarcodeField !== '') {
                barcodeFieldsSelected.push(this.labelPrintingData.firstBarcodeField);
            }
            if (this.labelPrintingData.secondBarcodeField !== '') {
                barcodeFieldsSelected.push(this.labelPrintingData.secondBarcodeField);
            }
            if (this.labelPrintingData.thirdBarcodeField !== '') {
                barcodeFieldsSelected.push(this.labelPrintingData.thirdBarcodeField);
            }

        }

        const labelsGeneratorInput = {
            fields: fieldsSelected,
            barcodeRequired: this.labelPrintingData.barcodeNeeded,
            automaticBarcode: this.labelPrintingData.barcodeGeneratedAutomatically,
            barcodeFields: barcodeFieldsSelected,
            fileName: this.labelPrintingData.filename,
            datasetId: undefined,
            studyId: undefined
        };

        this.service.download(this.fileType, labelsGeneratorInput).subscribe((response: any ) => {
           const fileName = this.fileDownloadHelper.getFileNameFromResponseContentDisposition(response);
           this.fileDownloadHelper.save(response.body, fileName);

        }, (error: HttpErrorResponse) => {
            this.handleError(error);

        });
    }

    private handleError(err: HttpErrorResponse): void {
        if ('application/json;charset=UTF-8' === err.headers.get('Content-Type')) {
            const reader = new FileReader();
            reader.addEventListener('loadend', (e) => {
                const error = JSON.parse(e.srcElement['result']);
                this.alertService.error('error.custom', { param: error.errors[0].message });
            });
            reader.readAsText(err.error);
        } else {
            this.alertService.error('error.general');
        }
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
    CSV = 'csv',
    PDF = 'pdf',
    EXCEL = 'xls'
}
