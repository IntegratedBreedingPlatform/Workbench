import { Component, Input, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { VariableDetailsContext } from './variable-details.context';
import { VariableType } from '../../shared/ontology/model/variable-type';

@Component({
    selector: 'jhi-details',
    templateUrl: './details.component.html',
})
export class DetailsComponent implements OnInit {

    variableDetails: VariableDetails;

    constructor(private jhiLanguageService: JhiLanguageService,
                private variableDetailsContext: VariableDetailsContext) {

    }

    ngOnInit(): void {
        this.variableDetailsContext.variableDetails.subscribe((variableDetails: VariableDetails) => this.variableDetails = variableDetails);
    }

    formatRoles(): string {
        if (this.variableDetails && this.variableDetails.variableTypes) {
            return this.variableDetails.variableTypes.map((value: VariableType) => value.name).join(', ' );
        }
        return '';
    }

    formatCropOntologyURL(): string {
        if (this.variableDetails && this.variableDetails.property) {
            return `http://www.cropontology.org/term/${this.variableDetails.property.cropOntologyId}/`;
        }
        return '';
    }

}
