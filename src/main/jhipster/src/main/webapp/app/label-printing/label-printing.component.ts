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
    labelTypesBarCode: LabelType[];
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
        const presetId = Number(this.presetSettingId);
        if (presetId !== 0) {
            const presetSetting = this.presetSettings.filter((preset) => preset.id === presetId)[0];
            this.fileType = this.getFileType(presetSetting.fileConfiguration.outputType);
            const labelTypeList = Object.assign([], this.labelTypesBarCode);
            const labelFieldsSelected = new Array();

            presetSetting.selectedFields.forEach(function(idsSelected) {
                const fieldsSelected: LabelType[] = new Array();
                labelTypeList.forEach(function(label: LabelType) {
                    const labelType = new LabelType(label.title, label.key, []);
                    label.fields.forEach(function(item, index, object) {
                        if (idsSelected.indexOf(item.id) > -1) {
                            labelType.fields.push(item);
                            object.splice(index, 0);
                        }
                    });
                    fieldsSelected.push(labelType);
                });
                labelFieldsSelected.push(fieldsSelected);
            });

            this.labelTypes = Object.assign([], labelTypeList);
            this.fieldsSelected = Object.assign([], labelFieldsSelected);

            setTimeout(() => {
                $('#leftSelectedFields').empty();
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
        const presetSetting = this.presetSettings.filter((preset) => preset.id === Number(this.presetSettingId))[0];
        this.modalTitle = 'Delete Label Printing Setting?';
        this.modalMessage = 'Are you sure you want to delete ' + presetSetting.name + ' ?';
        this.modalService.open('modal-confirm');
        this.proceed = function deletePreset(): void {
            this.service.deletePreset(this.presetSettingId).subscribe(() => {
                this.alertService.success('label-printing.delete.label.settings.success');
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

    savePresets() {
        const preset: PresetSetting = new PresetSetting();
        const fileConfiguration: FileConfiguration = new FileConfiguration();
        const barcodeSetting: BarcodeSetting = new BarcodeSetting();
        const selectedFields = [];

        fileConfiguration.outputType = this.fileType.toString();

        selectedFields.push($('#leftSelectedFields').sortable('toArray'));
        if (this.fileType === FileType.PDF) {
            selectedFields.push($('#rightSelectedFields').sortable('toArray'));
        }

        barcodeSetting.automaticBarcode = this.labelPrintingData.barcodeGeneratedAutomatically;
        barcodeSetting.barcodeNeeded = this.labelPrintingData.barcodeNeeded;
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
        preset.toolSection = 'DATASET_LABEL_PRINTING_PRESET';
        preset.toolId = 23;
        preset.name = this.labelPrintingData.settingsName;
        preset.type = 'LabelPrintingPreset';
        preset.selectedFields = selectedFields;
        preset.barcodeSetting = barcodeSetting;
        preset.fileConfiguration = fileConfiguration;

        this.service.addPreset(preset).subscribe((presetSetting) => {
            this.presetSettings.push(presetSetting);
                this.presetSettingId = presetSetting.id;
            this.alertService.success('label-printing.save.label.settings.success');
        }, (response) => {
            if (response.error.errors[0].message) {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            } else {
                this.alertService.error('error.general');
            }
        });
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
