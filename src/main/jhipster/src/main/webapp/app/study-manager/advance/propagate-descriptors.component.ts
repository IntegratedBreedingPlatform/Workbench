import { Component, Input, OnInit } from '@angular/core';
import { TemplateModel } from './template.model';
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { SaveTemplateComponent } from './save-template.component';
import { TemplateService } from './template.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { AlertService } from '../../shared/alert/alert.service';
import { ParamContext } from '../../shared/service/param.context';
import { AdvanceType } from './abstract-advance.component';
import { SELECT_INSTANCES } from '../../app.events';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-propagate-descriptors',
    templateUrl: './propagate-descriptors.component.html'
})
export class PropagateDescriptorsComponent implements OnInit {
    eventSubscriber: Subscription;
    @Input() isDescriptorsPropagationView?: boolean;
    selectedDescriptors: VariableDetails[] = [];
    propagateDescriptors: boolean;
    overrideDescriptorsLocation: boolean;
    locationOverrideId: number;
    selectedDescriptorIds: number[] = [];
    @Input() advanceType: AdvanceType;
    @Input() selectedDatasetId: number;

    isLoading: boolean;
    loadSavedSettings: boolean;
    VARIABLE_TYPE_IDS = [VariableTypeEnum.GERMPLASM_ATTRIBUTE, VariableTypeEnum.GERMPLASM_PASSPORT];
    DEFAULT_PASSPORT_DESCRIPTORS = ['PLOTCODE_AP_TEXT', 'PLOT_NUMBER_AP_TEXT', 'INSTANCE_NUMBER_AP_TEXT', 'REP_NUMBER_AP_TEXT', 'PLANT_NUMBER_AP_TEXT'];
    variable: VariableDetails;

    templateId: number;
    templates: TemplateModel[];

    constructor(public paramContext: ParamContext,
                public modalService: NgbModal,
                public templateService: TemplateService,
                public activeModal: NgbActiveModal,
                public variableService: VariableService,
                public alertService: AlertService,
                private eventManager: JhiEventManager) {
    }

    ngOnInit(): void {
        this.loadPresets();
        this.eventSubscriber = this.eventManager.subscribe('showPropagateDescriptorsView', (event) => {
            this.isDescriptorsPropagationView = true;
        });
    }

    selectVariable(variable: VariableDetails) {
        this.variable = variable;
    }

    addDescriptor() {
        if (!this.selectedDescriptorIds.includes(parseInt(this.variable.id, 10))) {
            const descriptors: VariableDetails[] = this.cloneDescriptorsArray();
            descriptors.push(this.variable);
            this.selectedDescriptors = descriptors;
            this.selectedDescriptorIds.push(parseInt(this.variable.id, 10));
        } else {
            this.alertService.warning('advance-study.attributes.table.variable.existing.warning', { param: this.variable.name });
        }
        this.variable = null;
    }

    exitDescriptorsPropagationView() {
        this.isDescriptorsPropagationView = false;
        this.eventManager.broadcast({ name: 'exitDescriptorsPropagationView', content: {
                isDescriptorsPropagationView: this.isDescriptorsPropagationView,
                propagateDescriptors: this.propagateDescriptors,
                selectedDescriptorIds: this.selectedDescriptorIds,
                overrideDescriptorsLocation: this.overrideDescriptorsLocation,
                locationOverrideId: this.locationOverrideId
            }});
    }

    removeFromSelectedDescriptors(toRemove: VariableDetails) {
        this.selectedDescriptorIds = this.selectedDescriptorIds.filter((id) => id !== parseInt(toRemove.id, 10));
        const lastItem: VariableDetails = this.selectedDescriptors[this.selectedDescriptors.length - 1];
        this.selectedDescriptors = this.selectedDescriptors.filter((descriptor) => descriptor.id !== toRemove.id);

        if (lastItem.id === toRemove.id) {
            this.selectedDescriptors = this.cloneDescriptorsArray();
        }
    }

    cloneDescriptorsArray(): VariableDetails[] {
        const descriptors: VariableDetails[] = [];
        // clone selectedDescriptors(table contents) to properly load UI styling of table
        this.selectedDescriptors.forEach((variable) => {
            const variableCopy: VariableDetails = { ...variable };
            descriptors.push(variableCopy);
        });
        return descriptors;
    }

    isPropagationInvalid() {
        if (this.propagateDescriptors && this.selectedDescriptorIds.length === 0) {
            return true;
        }

        if (this.overrideDescriptorsLocation && this.locationOverrideId === null) {
            return true;
        }
        return false;
    }

    deleteSelectedSetting() {
        const templateModel = this.templates.filter((template) => template.templateId === Number(this.templateId))[0];
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Delete preset?';
        confirmModalRef.componentInstance.message = 'Are you sure you want to delete ' + templateModel.templateName + ' ?';
        confirmModalRef.result.then(() => {
            this.templateService.deleteTemplate(this.templateId).subscribe(() => {
                this.alertService.success('advance-study.attributes.preset.delete.success');
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

    applySelectedSetting() {
        if (Number(this.templateId) !== 0) {
            this.selectedDescriptors = [];
            this.selectedDescriptorIds = [];
            const templateModel = this.templates.filter((template) => template.templateId === Number(this.templateId))[0];
            const variableIds = [];
            templateModel.templateDetails.forEach((templateDetail) => {
                variableIds.push(templateDetail.variableId.toString());
            });
            this.variableService.filterVariables({ variableIds,
                variableTypeIds: [VariableTypeEnum.GERMPLASM_PASSPORT.toString(), VariableTypeEnum.GERMPLASM_ATTRIBUTE.toString()],
                showObsoletes: false}).subscribe((variables) => {
                this.selectedDescriptors = variables;
                variables.forEach((variable) => {
                    this.selectedDescriptorIds.push(parseInt(variable.id, 10));
                });
            });
        }
    }

    private loadPresets() {
        this.isLoading = true;
        this.templateService.getAllTemplates().subscribe((templates) => {
            this.templates = templates;
            this.templateId = 0;
            this.isLoading = false;
        }, (response) => {
            if (response.error.errors[0].message) {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            } else {
                this.alertService.error('error.general');
            }
        });
    }

    saveTemplate() {
        if (Number(this.templateId) !== 0 ) {
            this.updateTemplate();
        } else {
            const template: TemplateModel = new TemplateModel();
            template.templateDetails = [];
            this.selectedDescriptors.forEach((variable) => {
                template.templateDetails.push({
                    variableId: parseInt(variable.id, 10),
                    name: variable.name,
                    type: variable.variableTypes[0].name
                });
            })
            template.programUUID = this.paramContext.programUUID;
            template.templateType = 'DESCRIPTORS';
            const saveTemplateModalRef = this.modalService.open(SaveTemplateComponent as Component);
            saveTemplateModalRef.result.then((templateName) => {
                template.templateName = templateName;
                this.templateService.addTemplate(template).subscribe((savedTemplate ) => {
                    this.alertService.success('advance-study.attributes.preset.update.success');
                    this.templates.push(savedTemplate);
                    this.templateId = savedTemplate.templateId;
                    this.loadSavedSettings = true;
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

    updateTemplate() {
        const templateModel = this.templates.filter((template) => template.templateId === Number(this.templateId))[0];
        templateModel.templateDetails = [];
        this.selectedDescriptors.forEach((variable) => {
            templateModel.templateDetails.push({
                variableId: parseInt(variable.id, 10),
                name: variable.name,
                type: variable.variableTypes[0].name
            });
        })
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Confirmation';
        confirmModalRef.componentInstance.message = '"' + templateModel.templateName + '" already exists, do you wish to overwrite the setting? ';
        confirmModalRef.result.then(() => {
            this.templateService.updateTemplate(templateModel).subscribe((res: void) => {
                this.alertService.success('advance-study.attributes.preset.update.success');
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

    back() {
        let advanceType = 'study';
        if (this.advanceType === AdvanceType.SAMPLES) {
            advanceType = 'samples';
        }
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: SELECT_INSTANCES, advanceType, selectedDatasetId: this.selectedDatasetId }, '*');
        }
    }
}
