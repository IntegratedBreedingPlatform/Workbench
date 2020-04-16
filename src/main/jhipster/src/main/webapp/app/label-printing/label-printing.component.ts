import { AfterViewInit, Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LabelsNeededSummary, LabelType, LabelPrintingData, PresetSetting, BarcodeSetting, FileConfiguration } from './label-printing.model';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { LabelPrintingContext } from './label-printing.context';
import { LabelPrintingService } from './label-printing.service';
import { FileDownloadHelper } from '../entities/sample/file-download.helper';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalService } from '../shared/modal/modal.service';

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
    labelTypesOrig: LabelType[];
    FILE_TYPES = FileType;
    fileType: FileType = FileType.NONE;
    presetSettingId: number;
    loadSavedSettings = false;
    fieldsSelected: LabelType[];
    presetSettings: PresetSetting[];
    modalTitle: string;
    modalMessage: string;

    constructor(private route: ActivatedRoute,
                private context: LabelPrintingContext,
                private service: LabelPrintingService,
                private languageService: JhiLanguageService,
                private fileDownloadHelper: FileDownloadHelper,
                private alertService: JhiAlertService,
                private modalService: ModalService) {
    }

    proceed(): void {
    }

    ngOnInit() {
        const params = this.route.snapshot.queryParams;
        this.context.datasetId = params['datasetId'];
        this.context.studyId = params['studyId'];
        this.context.programId = params['programId'];
        this.context.printingLabelType = params['printingLabelType'];
        this.context.searchRequestId = params['searchRequestId'];

        if (this.hasHeader()) {
            this.service.getLabelsNeededSummary().subscribe((summary: any) => {
                this.labelsNeededSummary = summary;
            });
        }
        this.service.getOriginResourceMetadada().subscribe((originResourceMetadata) => {
            this.metadata = new Map(Object.entries(originResourceMetadata.metadata));
            this.metadataKeys = Array.from(this.metadata.keys());
            this.labelPrintingData.filename = originResourceMetadata.defaultFileName;
        });
        this.service.getAvailableLabelFields().subscribe((labelTypes) => {
            this.labelTypes = labelTypes;
            this.labelTypesOrig = labelTypes.map((x) => Object.assign({}, x));
        });

        this.service.getAllPresets(this.getToolSection()).subscribe((PresetSettings) => {
            this.presetSettings = PresetSettings;
        });
        this.presetSettingId = 0;
        this.labelPrintingData.sizeOfLabelSheet = '1';
        this.labelPrintingData.numberOfRowsPerPage = 7;
    }

    ngAfterViewInit() {
        this.initDragAndDrop();
    }

    hasHeader() {
        return typesWithHeaderDetails.indexOf(this.context.printingLabelType) !== -1;
    }

    isExcelEnabled() {
        return this.context.printingLabelType === LabelPrintingType.LOT;
    }

    /**
     * Indicates if the export is for label printing
     */
    isLabelPrinting() {
        return this.fileType === FileType.PDF;
    }

    applySelectedSetting() {
        const presetId = Number(this.presetSettingId);
        if (presetId !== 0) {
            const presetSetting = this.presetSettings.filter((preset) => preset.id === presetId)[0];
            this.fileType = this.getFileType(presetSetting.fileConfiguration.outputType);
            const labelTypeList = this.labelTypesOrig.map((x) => Object.assign({}, x));
            const labelFieldsSelected = new Array();

            presetSetting.selectedFields.forEach(function(idsSelected) {
                const fieldsSelected: LabelType[] = new Array();
                labelTypeList.forEach(function(label: LabelType) {
                    const labelType = new LabelType(label.title, label.key, []);
                    labelType.fields = label.fields.filter((field) => idsSelected.indexOf(field.id) > -1);
                    fieldsSelected.push(labelType);
                    const filteredList = label.fields.filter((field) => labelType.fields.indexOf(field) <= -1);
                    label.fields = filteredList;
                });
                labelFieldsSelected.push(fieldsSelected);
            });

            this.labelTypes = labelTypeList.map((x) => Object.assign({}, x));
            this.fieldsSelected = labelFieldsSelected;

            setTimeout(() => {
                $('#leftSelectedFields').empty();
                $('#rightSelectedFields').empty();

                let listElem = '#leftSelectedFields';
                labelFieldsSelected.forEach(function(fieldsList: LabelType[]) {
                    fieldsList.forEach(function(labelsType: LabelType) {
                        const key = labelsType.key;
                        labelsType.fields.forEach(function(field) {
                            $('<li/>').addClass('list-group-item text-truncate ui-sortable-handle') //
                                .attr('id', field.id).attr('data-label-type-key', key) //
                                .text(field.name).appendTo(listElem);
                        });
                    });
                    if (labelFieldsSelected.length > 1) {
                        listElem = '#rightSelectedFields';
                    }
                });
            });

            this.initDragAndDrop();
            if (presetSetting.fileConfiguration.outputType === FileType.PDF.toString()) {
                this.labelPrintingData.numberOfRowsPerPage = presetSetting.fileConfiguration.numberOfRowsPerPage;
                this.labelPrintingData.sizeOfLabelSheet = presetSetting.fileConfiguration.sizeOfLabelSheet;
            }

            this.labelPrintingData.settingsName = presetSetting.name;
            this.labelPrintingData.barcodeNeeded = presetSetting.barcodeSetting.barcodeNeeded;
            this.labelPrintingData.barcodeGeneratedAutomatically = presetSetting.barcodeSetting.automaticBarcode;
            this.labelPrintingData.includeHeadings = presetSetting.includeHeadings;

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
        const presetSetting = this.presetSettings.filter((preset) => preset.id === Number(this.presetSettingId))[0];
        this.modalTitle = 'Delete preset?';
        this.modalMessage = 'Are you sure you want to delete ' + presetSetting.name + ' ?';
        this.modalService.open('modal-confirm');
        this.proceed = function deletePreset(): void {
            this.service.deletePreset(this.presetSettingId).subscribe(() => {
                this.alertService.success('label-printing.delete.preset.success');
                this.service.getAllPresets().subscribe((PresetSettings) => {
                    this.presetSettings = PresetSettings;
                    this.presetSettingId = 0;
                });
            }, (response) => {
                if (response.error.errors[0].message) {
                    this.alertService.error('error.custom', { param: response.error.errors[0].message });
                } else {
                    this.alertService.error('error.general');
                }
            });
        }
    }

    reset() {
        this.labelTypes = this.labelTypesOrig.map((x) => Object.assign({}, x));
        this.labelPrintingData.barcodeNeeded = false;
        this.labelPrintingData.includeHeadings = true;
        $('#leftSelectedFields').empty();
        $('#rightSelectedFields').empty();
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
                    if (receiver.hasClass('print-fields') && this.fileType === FileType.PDF && receiver.children().length > 5) {
                        $(ui.sender).sortable('cancel');
                    }
                }
            });
        });
    }

    export() {
        const fieldsSelected = [];
        const barcodeFieldsSelected = [];

        fieldsSelected.push($('#leftSelectedFields').sortable('toArray'));
        if (this.fileType === FileType.PDF) {
            fieldsSelected.push($('#rightSelectedFields').sortable('toArray'));
        }

        if (this.labelPrintingData.barcodeNeeded && !this.labelPrintingData.barcodeGeneratedAutomatically) {
            if (Number(this.labelPrintingData.firstBarcodeField) !== 0) {
                barcodeFieldsSelected.push(this.labelPrintingData.firstBarcodeField);
            }
            if (Number(this.labelPrintingData.secondBarcodeField) !== 0) {
                barcodeFieldsSelected.push(this.labelPrintingData.secondBarcodeField);
            }
            if (Number(this.labelPrintingData.thirdBarcodeField) !== 0) {
                barcodeFieldsSelected.push(this.labelPrintingData.thirdBarcodeField);
            }

        }

        const labelsGeneratorInput = {
            fields: fieldsSelected,
            barcodeRequired: this.labelPrintingData.barcodeNeeded,
            automaticBarcode: this.labelPrintingData.barcodeGeneratedAutomatically,
            barcodeFields: barcodeFieldsSelected,
            sizeOfLabelSheet: this.labelPrintingData.sizeOfLabelSheet,
            numberOfRowsPerPageOfLabel: this.labelPrintingData.numberOfRowsPerPage,
            includeHeadings: this.labelPrintingData.includeHeadings,
            fileName: this.labelPrintingData.filename,
            datasetId: undefined,
            studyId: undefined
        };

        this.proceed = function donwloadPrintingLabel(): void {
            this.service.download(this.fileType, labelsGeneratorInput).subscribe((response: any ) => {
                const fileName = this.fileDownloadHelper.getFileNameFromResponseContentDisposition(response);
                this.fileDownloadHelper.save(response.body, fileName);

            }, (error: HttpErrorResponse) => {
                this.handleError(error);

            });
        }

        if (Number(this.presetSettingId) === 0) {
            this.modalTitle = 'Confirmation';
            this.modalMessage = 'Proceed export label without saving label printing setting?';
            this.modalService.open('modal-confirm');
        } else {
            this.proceed();
        }
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

        fileConfiguration.outputType = this.fileType.toString();

        selectedFields.push($('#leftSelectedFields').sortable('toArray'));
        if (this.fileType === FileType.PDF) {
            selectedFields.push($('#rightSelectedFields').sortable('toArray'));
            fileConfiguration.sizeOfLabelSheet = this.labelPrintingData.sizeOfLabelSheet;
            fileConfiguration.numberOfRowsPerPage = this.labelPrintingData.numberOfRowsPerPage;
        }

        barcodeSetting.barcodeNeeded = this.labelPrintingData.barcodeNeeded;
        barcodeSetting.automaticBarcode = barcodeSetting.barcodeNeeded ? this.labelPrintingData.barcodeGeneratedAutomatically : false;

        if (this.labelPrintingData.barcodeNeeded && !this.labelPrintingData.barcodeGeneratedAutomatically) {
            barcodeSetting.barcodeFields = new Array();
            if (Number(this.labelPrintingData.firstBarcodeField) !== 0) {
                barcodeSetting.barcodeFields.push(Number(this.labelPrintingData.firstBarcodeField));
            }
            if (Number(this.labelPrintingData.secondBarcodeField) !== 0) {
                barcodeSetting.barcodeFields.push(Number(this.labelPrintingData.secondBarcodeField));
            }
            if (Number(this.labelPrintingData.thirdBarcodeField) !== 0) {
                barcodeSetting.barcodeFields.push(Number(this.labelPrintingData.thirdBarcodeField));
            }

        }

        preset.programUUID = this.context.programId;
        preset.toolSection = this.getToolSection();
        preset.toolId = 23;
        preset.name = this.labelPrintingData.settingsName;
        preset.type = 'LabelPrintingPreset';
        preset.selectedFields = selectedFields;
        preset.barcodeSetting = barcodeSetting;
        preset.fileConfiguration = fileConfiguration;
        preset.includeHeadings = this.labelPrintingData.includeHeadings;

        this.service.addPreset(preset).subscribe((presetSetting) => {
            this.presetSettings.push(presetSetting);
                this.presetSettingId = presetSetting.id;
            this.alertService.success('label-printing.save.preset.success');
        }, (response) => {
            if (response.error.errors[0].message) {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            } else {
                this.alertService.error('error.general');
            }
        });
    }

    private getToolSection() {
        switch (this.context.printingLabelType) {
            case LabelPrintingType.SUBOBSERVATION_DATASET:
                return 'DATASET_LABEL_PRINTING_PRESET';
            case LabelPrintingType.LOT:
                return 'LOT_LABEL_PRINTING_PRESET';
            default:
                return;
        }
    }

    getFileType(extension: string): FileType {
        switch (extension) {
            case FileType.CSV.toString():
                return FileType.CSV;
            case FileType.PDF.toString():
                return FileType.PDF;
            case FileType.EXCEL.toString():
                return FileType.EXCEL;
            default:
                return FileType.NONE;
        }
    }

    getChooseLabelDescription() {
        if (!this.labelTypes || !this.labelTypes.length) {
            return;
        }

        const from = this.labelTypes.map((l) => `<strong>${l.title}</strong>`).join(' and ');
        let to = '<strong>Selected Fields</strong>';
        if (this.fileType === FileType.PDF) {
            to = '<strong>Left Side Fields</strong> and <strong>Right Side Fields</strong>';
        }
        return `Drag fields from the ${from} into the ${to} to add them to your export file.`;
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

/** aka printingLabelType in params */
export enum LabelPrintingType {
    SUBOBSERVATION_DATASET = 'SubObservationDataset',
    LOT = 'Lot'
}

const typesWithHeaderDetails: LabelPrintingType[] = [
    LabelPrintingType.SUBOBSERVATION_DATASET
]
