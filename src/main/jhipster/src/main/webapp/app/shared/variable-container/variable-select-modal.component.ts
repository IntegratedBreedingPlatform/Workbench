import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { VariableService } from '../ontology/service/variable.service';
import { VariableValidationService } from '../ontology/service/variable-validation.service';
import { VariableTypeEnum } from '../ontology/variable-type.enum';
import { VariableDetails } from '../ontology/model/variable-details';
import { LocationModel } from '../location/model/location.model';
import { DataTypeEnum } from '../ontology/data-type.enum';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'jhi-variable-select-modal',
    templateUrl: './variable-select-modal.component.html'
})
export class VariableSelectModalComponent implements OnInit, OnDestroy {

    isLoading: boolean;
    germplasmUUID: string;

    VARIABLE_TYPE_IDS;
    variable: VariableDetails;

    constructor(public activeModal: NgbActiveModal,
                public translateService: TranslateService,
                private route: ActivatedRoute,
                private eventManager: JhiEventManager,
                private variableService: VariableService,
                private validationService: VariableValidationService,
                private alertService: JhiAlertService) {

        const queryParamMap = this.route.snapshot.queryParamMap;
        this.germplasmUUID = queryParamMap.get('germplasmUUID');
        this.VARIABLE_TYPE_IDS = [VariableTypeEnum.ENTRY_DETAILS];

    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.activeModal.close(this.variable);
    }

    selectVariable(variable: VariableDetails) {
        this.variable = variable;
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.variable;
    }

}
