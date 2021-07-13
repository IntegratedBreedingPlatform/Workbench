import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { VariableDetailsContext } from './variable-details.context';
import { DataTypeEnum } from '../../shared/ontology/data-type.enum';

@Component({
    selector: 'jhi-valid-values',
    templateUrl: './valid-values.component.html',
})
export class ValidValuesComponent implements OnInit {

    dataType = DataTypeEnum;
    variableDetails: VariableDetails;

    constructor(private jhiLanguageService: JhiLanguageService,
                private variableDetailsContext: VariableDetailsContext
    ) {

    }

    ngOnInit(): void {
        this.variableDetailsContext.variableDetails.subscribe((variableDetails: VariableDetails) => this.variableDetails = variableDetails);
    }

}
