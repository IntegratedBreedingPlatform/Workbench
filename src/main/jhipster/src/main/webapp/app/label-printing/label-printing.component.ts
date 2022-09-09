import { Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BarcodeSetting, FileConfiguration, LabelPrintingData, LabelsNeededSummary, LabelType, PresetSetting, Sortable } from './label-printing.model';
import { JhiLanguageService } from 'ng-jhipster';
import { LabelPrintingContext } from './label-printing.context';
import { LabelPrintingService } from './label-printing.service';
import { FileDownloadHelper } from '../entities/sample/file-download.helper';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertService } from '../shared/alert/alert.service';
import { HelpService } from '../shared/service/help.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalConfirmComponent } from '../shared/modal/modal-confirm.component';
import { ParamContext } from '../shared/service/param.context';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HELP_LABEL_PRINTING_GERMPLASM_LIST_MANAGER, HELP_LABEL_PRINTING_GERMPLASM_MANAGER,
    HELP_LABEL_PRINTING_INVENTORY_MANAGER, HELP_LABEL_PRINTING_STUDY_MANAGER } from '../app.constants';
import { CdkDragDrop, moveItemInArray, transferArrayItem, CdkDrag, CdkDropList } from '@angular/cdk/drag-drop';

declare const $: any;

@Component({
    selector: 'jhi-label-printing',
    templateUrl: './label-printing.component.html',
    styleUrls: ['./label-printing.component.css']
})
export class LabelPrintingComponent implements OnInit {
    initComplete: boolean;

    labelPrintingData: LabelPrintingData = new LabelPrintingData();
    labelsNeededSummary: LabelsNeededSummary;
    metadata: Map<string, string>;
    metadataKeys: string[];
    labelTypes: LabelType[];
    labelTypesOrig: LabelType[];
    FILE_TYPES = FileType;
    fileType: FileType = FileType.NONE;
    selectedfileType: FileType = FileType.NONE;
    presetSettingId: number;
    loadSavedSettings = false;
    fieldsSelected: LabelType[];
    leftSideFields: LabelType;
    presetSettings: PresetSetting[];
    modalTitle: string;
    modalMessage: string;
    helpLink: string;

    sortableFields: Sortable[];
    sortBySelected: any = '';
    isLoading: boolean;
    defaultPresetSetting: PresetSetting;

    constructor(private route: ActivatedRoute,
                private context: LabelPrintingContext,
                private service: LabelPrintingService,
                private languageService: JhiLanguageService,
                private fileDownloadHelper: FileDownloadHelper,
                private alertService: AlertService,
                public activeModal: NgbActiveModal,
                private modalService: NgbModal,
                private paramContext: ParamContext,
                private helpService: HelpService) {
        this.paramContext.readParams();
    }

    proceed(): void {
    }

    ngOnInit() {
        const params = this.route.snapshot.queryParams;
        this.context.datasetId = params['datasetId'];
        this.context.studyId = params['studyId'];
        this.context.printingLabelType = params['printingLabelType'];
        this.context.searchRequestId = params['searchRequestId'];
        this.context.listId = params['listId'];

        let labelsNeededPromise = Promise.resolve({});
        if (this.hasHeader()) {
            labelsNeededPromise = this.service.getLabelsNeededSummary().toPromise();
            labelsNeededPromise.then((summary: any) => {
                this.labelsNeededSummary = summary;
            });
        }

        const metadataPromise = this.service.getOriginResourceMetadada().toPromise();
        metadataPromise.then((originResourceMetadata) => {
            this.metadata = new Map(Object.entries(originResourceMetadata.metadata));
            this.metadataKeys = Array.from(this.metadata.keys());
            this.labelPrintingData.filename = originResourceMetadata.defaultFileName;
        }).catch((response) => {
            this.alertService.error('error.custom', { param: response.error.errors[0].message });
            this.initComplete = true;
            return;
        });

        const fieldsPromise = this.service.getAvailableLabelFields().toPromise();
        fieldsPromise.then((labelTypes) => {
            this.labelTypes = labelTypes;
            this.labelTypesOrig = labelTypes.map((x) => Object.assign({}, x));
        });

        this.leftSideFields = new LabelType();
        this.leftSideFields.fields = new Array();
        const presetPromise = this.loadPresets();

        const sorteableFieldsPromise = this.service.getSortableFields().toPromise();
        sorteableFieldsPromise.then((sortables) => {
            this.sortableFields = sortables;
        });

        const defaultSelectionPromise = this.service.getDefaultSettings().toPromise();
        defaultSelectionPromise.then((defaultSelection) => {
            this.defaultPresetSetting = defaultSelection;
        }).catch((response) => {
            this.alertService.error('error.custom', { param: response.error.errors[0].message });
            this.initComplete = true;
            return;
        });

        Promise.all([
            labelsNeededPromise,
            metadataPromise,
            fieldsPromise,
            presetPromise,
            sorteableFieldsPromise,
            defaultSelectionPromise
        ]).then(() => {
            this.initComplete = true;
            if (this.defaultPresetSetting) {
                this.loadPresetSetting(this.defaultPresetSetting);
            }
        });

        this.labelPrintingData.sizeOfLabelSheet = '1';
        this.labelPrintingData.numberOfRowsPerPage = 7;

        // Get helplink url
        if (!this.helpLink || !this.helpLink.length) {

            this.helpService.getHelpLink(this.getHelpLinkString()).toPromise().then((response) => {
                this.helpLink = response.body;
            }).catch((error) => {});
        }
    }

    getHelpLinkString() {
        if (this.context.printingLabelType === LabelPrintingType.GERMPLASM) {
            return HELP_LABEL_PRINTING_GERMPLASM_MANAGER;
        } else if (this.context.printingLabelType === LabelPrintingType.GERMPLASM_LIST) {
            return HELP_LABEL_PRINTING_GERMPLASM_LIST_MANAGER;
        } else if (this.context.printingLabelType === LabelPrintingType.SUBOBSERVATION_DATASET
            || this.context.printingLabelType === LabelPrintingType.OBSERVATION_DATASET ) {
            return HELP_LABEL_PRINTING_STUDY_MANAGER;
        } else if (this.context.printingLabelType === LabelPrintingType.LOT) {
            return HELP_LABEL_PRINTING_INVENTORY_MANAGER;
        }
    }

    hasHeader() {
        return typesWithHeaderDetails.indexOf(this.context.printingLabelType) !== -1;
    }

    isForGermplasmListLabelPrinting() {
        return this.context.printingLabelType === LabelPrintingType.GERMPLASM_LIST;
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
            this.loadPresetSetting(this.presetSettings.filter((preset) => preset.id === presetId)[0]);
        }
    }

    loadPresetSetting(presetSetting: PresetSetting) {
        this.fileType = this.getFileType(presetSetting.fileConfiguration.outputType);
        this.selectedfileType = this.fileType;

        const labelTypeList = this.labelTypesOrig.map((x) => Object.assign({}, x));
        const labelFieldsSelected = new Array();

        presetSetting.selectedFields.forEach((idsSelected) => {
            const fieldsSelected: LabelType[] = new Array();
            labelTypeList.forEach((label: LabelType) => {
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
        this.sortBySelected = (presetSetting.sortBy) ? presetSetting.sortBy : '';

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

    deleteSelectedSetting() {
        const presetSetting = this.presetSettings.filter((preset) => preset.id === Number(this.presetSettingId))[0];
        this.modalTitle = 'Delete preset?';
        this.modalMessage = 'Are you sure you want to delete ' + presetSetting.name + ' ?';
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = this.modalTitle;
        confirmModalRef.componentInstance.message = this.modalMessage;
        confirmModalRef.result.then(() => {
            this.service.deletePreset(this.presetSettingId).subscribe(() => {
                this.alertService.success('label-printing.delete.preset.success');
                this.loadPresets();
            }, (response) => {
                if (response.error.errors[0].message) {
                    this.alertService.error('error.custom', { param: response.error.errors[0].message });
                } else {
                    this.alertService.error('error.general');
                }
            });
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
        return;
    }

    private loadPresets() {
        return this.service.getAllPresets(this.getToolSection()).subscribe((PresetSettings) => {
            this.presetSettings = PresetSettings;
            this.presetSettingId = 0;
        });
    }

    reset() {
        if ((this.selectedfileType !== this.fileType && //
                (this.selectedfileType === FileType.PDF || this.selectedfileType === FileType.NONE) && //
                (this.fileType === FileType.EXCEL || this.fileType === FileType.CSV))
            || this.fileType === FileType.NONE || this.fileType === FileType.PDF) {
            this.labelTypes = this.labelTypesOrig.map((x) => Object.assign({}, x));
            this.labelPrintingData.barcodeNeeded = false;
            this.labelPrintingData.includeHeadings = true;
            this.sortBySelected = '';
            $('#leftSelectedFields').empty();
            $('#rightSelectedFields').empty();
        }
        this.selectedfileType =  this.fileType;
    }

    reloadFields(selected) {
        const labelTypeList = this.labelTypesOrig.map((x) => Object.assign({}, x));
        const labelFieldsSelected = new Array();
        selected.forEach((idsSelected) => {
            const fieldsSelected: LabelType[] = new Array();
            labelTypeList.forEach((label: LabelType) => {
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
            labelFieldsSelected.forEach((fieldsList: LabelType[]) => {
                fieldsList.forEach((labelsType: LabelType) => {
                    const key = labelsType.key;
                    labelsType.fields.forEach((field) => {
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
    }

    resetSelectFields($event) {
        $event.preventDefault();
        const fieldsSelected: number[][] = [];
/*        $(selectedFields).empty();

        if (selectedFields === '#leftSelectedFields' && this.fileType === FileType.PDF) {
            if ($('#rightSelectedFields').sortable('toArray').length > 0) {
                fieldsSelected.push([], $('#rightSelectedFields').sortable('toArray').map((i) => Number(i)));
            }
        } else {
            if ($('#leftSelectedFields').sortable('toArray').length > 0) {
                fieldsSelected.push($('#leftSelectedFields').sortable('toArray').map((i) => Number(i)));
            }
        }*/
        this.reloadFields(fieldsSelected);

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
            studyId: undefined,
            sortBy: !this.sortBySelected ? undefined : this.sortBySelected
        };

        this.proceed = function downloadPrintingLabel(): void {
            this.isLoading = true;
            this.service.download(this.fileType, labelsGeneratorInput).pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe((response: any) => {
                const fileName = this.fileDownloadHelper.getFileNameFromResponseContentDisposition(response);
                this.fileDownloadHelper.save(response.body, fileName);

            }, (error: HttpErrorResponse) => {
                this.handleError(error);
            });
        };

        if (Number(this.presetSettingId) === 0) {
            this.modalTitle = 'Confirmation';
            this.modalMessage = 'Proceed export label without saving label printing setting?';
            const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
            confirmModalRef.componentInstance.title = this.modalTitle;
            confirmModalRef.componentInstance.message = this.modalMessage;
            confirmModalRef.result.then(() => {
                this.proceed();
                this.activeModal.close();
            }, () => this.activeModal.dismiss());
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

        selectedFields.push($('#leftSelectedFields').sortable('toArray').map((i) => Number(i)));
        if (this.fileType === FileType.PDF) {
            selectedFields.push($('#rightSelectedFields').sortable('toArray').map((i) => Number(i)));
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

        preset.programUUID = this.paramContext.programUUID;
        preset.toolSection = this.getToolSection();
        preset.toolId = 23;
        preset.name = this.labelPrintingData.settingsName;
        preset.type = 'LabelPrintingPreset';
        preset.selectedFields = selectedFields;
        preset.barcodeSetting = barcodeSetting;
        preset.fileConfiguration = fileConfiguration;
        preset.includeHeadings = this.labelPrintingData.includeHeadings;
        if (this.sortBySelected) {
            preset.sortBy = this.sortBySelected;
        }

        const presetSetting = this.presetSettings.filter((x) => x.name === preset.name)[0];

        if (!presetSetting) {
            this.service.addPreset(preset).subscribe((presetCreated) => {
                this.presetSettings.push(presetCreated);
                this.presetSettingId = presetCreated.id;
                this.alertService.success('label-printing.save.preset.success');
            }, (response) => {
                if (response.error.errors[0].message) {
                    this.alertService.error('error.custom', { param: response.error.errors[0].message });
                } else {
                    this.alertService.error('error.general');
                }
            });
        } else {
            this.modalTitle = 'Confirmation';
            this.modalMessage = '"' + presetSetting.name + '" already exists, do you wish to overwrite the preset? ';
            const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
            confirmModalRef.componentInstance.title = this.modalTitle;
            confirmModalRef.componentInstance.message = this.modalMessage;
            confirmModalRef.result.then(() => {
                preset.id = presetSetting.id
                this.service.updatePreset(preset).subscribe((res: void) => {
                    presetSetting.selectedFields = preset.selectedFields;
                    presetSetting.barcodeSetting = preset.barcodeSetting;
                    presetSetting.fileConfiguration = preset.fileConfiguration;
                    presetSetting.includeHeadings = preset.includeHeadings;
                    presetSetting.sortBy = preset.sortBy;
                    this.alertService.success('label-printing.update.preset.success');
                }, (response) => {
                    if (response.error.errors[0].message) {
                        this.alertService.error('error.custom', { param: response.error.errors[0].message });
                    } else {
                        this.alertService.error('error.general');
                    }
                });
                this.activeModal.close();
            }, () => this.activeModal.dismiss());
        }

    }

    private getToolSection() {
        switch (this.context.printingLabelType) {
            case LabelPrintingType.OBSERVATION_DATASET:
            case LabelPrintingType.SUBOBSERVATION_DATASET:
                return 'DATASET_LABEL_PRINTING_PRESET';
            case LabelPrintingType.LOT:
                return 'LOT_LABEL_PRINTING_PRESET';
            case LabelPrintingType.GERMPLASM:
                return 'GERMPLASM_LABEL_PRINTING_PRESET';
            case LabelPrintingType.GERMPLASM_LIST:
                return 'GERMPLASM_LIST_LABEL_PRINTING_PRESET';
            case LabelPrintingType.STUDY_ENTRIES:
                return 'STUDY_ENTRIES_LABEL_PRINTING_PRESET';
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

    // TODO translateService
    getChooseLabelDescription() {
        if (!this.labelTypes || !this.labelTypes.length) {
            return;
        }

        const from = this.labelTypes.map((l) => `<strong>${l.title}</strong>`).join(' and ');
        let to = '<strong> Selected Fields </strong>';
        if (this.fileType === FileType.PDF) {
            to = `<strong> Left Side Fields </strong> and <strong> Right Side Fields </strong>`;
        }

        let description = `Drag fields from the ${from} into the ${to} to add them to your export file.`;

        if (this.fileType === FileType.PDF) {
            description += ' For PDF you can only add 5 fields per side';
        }

        return description;
    }

    back() {
        window.history.back();
    }

    drop(event: CdkDragDrop<{ id: number, name: string }[]>) {
        if (event.previousContainer === event.container) {
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
        } else if (event.container.id !== 'leftSideFields') {
            const title = event.container.id;
            const labelTypeOrg = this.labelTypesOrig.find((ltype) => ltype.title === title);
            const field = Object.assign({}, event.item.data)
            if (labelTypeOrg.fields.indexOf(field) === -1) {
                return;
            }
        } else {
            transferArrayItem(
                event.previousContainer.data,
                event.container.data,
                event.previousIndex,
                event.currentIndex,
            );
        }
    }

}

@Pipe({ name: 'allLabels' })
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
    OBSERVATION_DATASET = 'ObservationDataset',
    SUBOBSERVATION_DATASET = 'SubObservationDataset',
    LOT = 'Lot',
    GERMPLASM = 'Germplasm',
    GERMPLASM_LIST = 'Germplasm List',
    STUDY_ENTRIES = 'Study Entries'

}

const typesWithHeaderDetails: LabelPrintingType[] = [
    LabelPrintingType.OBSERVATION_DATASET, LabelPrintingType.SUBOBSERVATION_DATASET
];
