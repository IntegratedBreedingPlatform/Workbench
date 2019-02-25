import { AfterViewInit, Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LabelsNeededSummary, LabelType, LabelPrintingData, PresetSetting, BarcodeSetting, FileConfiguration } from './label-printing.model';
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
    labelTypesBarCode: LabelType[];
    FILE_TYPES = FileType;
    fileType: FileType = FileType.NONE;
    presetSettingId: number;
    loadSavedSettings = false;
    fieldsSelected: LabelType[];
    presetSettings: PresetSetting[];

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
            this.context.programId = params['programId'];
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
            this.labelTypesBarCode = Object.assign([], labelTypes);
        });

        this.service.getAllPresets().subscribe((PresetSettings) => {
            this.presetSettings = PresetSettings;
        });
        this.presetSettingId = 0;
    }

    ngAfterViewInit() {
        this.initDragAndDrop();
    }

    applySelectedSetting() {
        let presetId = this.presetSettingId;
        let presetSetting: PresetSetting = this.presetSettings.filter(preset => preset.id == presetId)[0];

        if (presetId != 0) {

            if (presetSetting.fileConfiguration.outputType === FileType.CSV.toString() || presetSetting.fileConfiguration.outputType === FileType.EXCEL.toString()) {
                this.fileType = presetSetting.fileConfiguration.outputType === FileType.CSV.toString() ? FileType.CSV : FileType.EXCEL;
                let idsSelected = presetSetting.selectedFields[0];
                let labelsSelected: LabelType[] = new Array();
                this.service.getAvailableLabelFields().subscribe((labelTypes) => {
                    this.labelTypes = labelTypes;
                    this.labelTypes.forEach(function (label: LabelType) {
                        let labelType = new LabelType();
                        labelType.title = label.title;
                        labelType.key = label.key;
                        labelType.fields = [];
                        label.fields.forEach(function (item, index, object) {
                            if (idsSelected.indexOf(item.id) > -1) {
                                labelType.fields.push(item);
                                object.splice(index, 1);

                            }
                        });
                        labelsSelected.push(labelType);

                    });

                    this.fieldsSelected = Object.assign([], labelsSelected);
                    setTimeout(() => {
                        $('#leftSelectedFields').empty();
                        this.addToUIFieldsList($('#leftSelectedFields'), this.fieldsSelected[0].fields, this.fieldsSelected[0].key);
                        this.addToUIFieldsList($('#leftSelectedFields'), this.fieldsSelected[1].fields, this.fieldsSelected[1].key);

                    });

            this.initDragAndDrop();
            if (presetSetting.fileConfiguration.outputType === FileType.PDF.toString()) {

            }

            this.labelPrintingData.settingsName = presetSetting.name;
            this.labelPrintingData.barcodeNeeded = presetSetting.barcodeSetting.barcodeNeeded;
            this.labelPrintingData.barcodeGeneratedAutomatically = presetSetting.barcodeSetting.automaticBarcode;

            if (this.labelPrintingData.barcodeNeeded && !this.labelPrintingData.barcodeGeneratedAutomatically) {
                this.labelPrintingData.firstBarcodeField = 0;
                this.labelPrintingData.secondBarcodeField = 0;
                this.labelPrintingData.thirdBarcodeField = 0;

                if (presetSetting.barcodeSetting.barcodeFields[0]) {
                    this.labelPrintingData.firstBarcodeField = presetSetting.barcodeSetting.barcodeFields[0];
                }
                if (presetSetting.barcodeSetting.barcodeFields[1]) {
                    this.labelPrintingData.secondBarcodeField = presetSetting.barcodeSetting.barcodeFields[1];
                }
                if (presetSetting.barcodeSetting.barcodeFields[2]) {
                    this.labelPrintingData.thirdBarcodeField = presetSetting.barcodeSetting.barcodeFields[2];
                }

            }
        }
    }

    deleteSelectedSetting() {
        this.service.deletePreset(this.presetSettingId).subscribe(() => {
            this.alertService.success('label-printing.delete.label.settings.success');
            this.service.getAllPresets().subscribe((PresetSettings) => {
                this.presetSettings = PresetSettings;
                this.presetSettingId = 0;
                this.fileType = FileType.NONE;
            });
        }, (response) => {
            if (response.error.errors[0].message) {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            } else {
                this.alertService.error('error.general');
            }
        });
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
            if (this.labelPrintingData.firstBarcodeField !== 0) {
                barcodeFieldsSelected.push(this.labelPrintingData.firstBarcodeField);
            }
            if (this.labelPrintingData.secondBarcodeField !== 0) {
                barcodeFieldsSelected.push(this.labelPrintingData.secondBarcodeField);
            }
            if (this.labelPrintingData.thirdBarcodeField !== 0) {
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

    savePresets() {
        const preset: PresetSetting = new PresetSetting();
        const fileConfiguration: FileConfiguration = new FileConfiguration();
        const barcodeSetting: BarcodeSetting = new BarcodeSetting();
        const selectedFields = [];

        fileConfiguration.outputType = this.fileType;

        selectedFields.push($('#leftSelectedFields').sortable('toArray'));
        if (this.fileType === FileType.EXCEL) {
            selectedFields.push($('#rightSelectedFields').sortable('toArray'));
        }

        barcodeSetting.automaticBarcode = this.labelPrintingData.barcodeGeneratedAutomatically;
        barcodeSetting.barcodeNeeded = this.labelPrintingData.barcodeNeeded;
        if (this.labelPrintingData.barcodeNeeded && !this.labelPrintingData.barcodeGeneratedAutomatically) {
            barcodeSetting.barcodeFields = [];
            if (this.labelPrintingData.firstBarcodeField !== 0) {
                barcodeSetting.barcodeFields.push(this.labelPrintingData.firstBarcodeField);
            }
            if (this.labelPrintingData.secondBarcodeField !== 0) {
                barcodeSetting.barcodeFields.push(this.labelPrintingData.secondBarcodeField);
            }
            if (this.labelPrintingData.thirdBarcodeField !== 0) {
                barcodeSetting.barcodeFields.push(this.labelPrintingData.thirdBarcodeField);
            }

        }

        preset.programUUID = this.context.programId;
        preset.toolId = 23;
        preset.toolSection = 'DATASET_LABEL_PRINTING_PRESET';
        preset.name = this.labelPrintingData.settingsName;
        preset.type = 'LabelPrintingPreset';
        preset.selectedFields = selectedFields;
        preset.barcodeSetting = barcodeSetting;
        preset.fileConfiguration = fileConfiguration;

        this.service.addPreset(preset).subscribe(() => {
            this.service.getAllPresets().subscribe((PresetSettings) => {
                this.presetSettings = PresetSettings;
            });
            this.alertService.success('label-printing.save.label.settings.success');
        }, (response) => {
            if (response.error.errors[0].message) {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            } else {
                this.alertService.error('error.general');
            }
        });
    }

    /**
     * Adds '<li/>' items to the UI given a map and the list
     * @param listElem
     * @param listMap
     * @param fieldsList
     */
    addToUIFieldsList(listElem, fieldsList, dataLabelTypeKey) {
        fieldsList.forEach(function (field) {
            $('<li/>').addClass('list-group-item text-truncate ui-sortable-handle').attr('id', field.id).attr('data-label-type-key', dataLabelTypeKey).text(field.name).appendTo(listElem);
        });
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
