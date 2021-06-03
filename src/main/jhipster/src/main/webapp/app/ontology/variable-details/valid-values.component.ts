import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { VariableDetailsContext } from './variable-details.context';

enum DataType {
    NUMERIC = 'Numeric',
    CATEGORICAL = 'Categorical',
    CHARACTER = 'Character'
}

@Component({
    selector: 'jhi-valid-values',
    templateUrl: './valid-values.component.html',
})
export class ValidValuesComponent implements OnInit {

    dataType = DataType;
    variableDetails: VariableDetails;

    constructor(private jhiLanguageService: JhiLanguageService,
                private variableDetailsContext: VariableDetailsContext
    ) {
        this.variableDetails = this.variableDetailsContext.variableDetails;
    }

    ngOnInit(): void {
    }

}
