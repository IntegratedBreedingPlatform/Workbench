import { Injectable } from '@angular/core';
import { VariableDetails } from '../../shared/ontology/model/variable-details';

@Injectable()
export class VariableDetailsContext {
    variableDetails: VariableDetails;
}
