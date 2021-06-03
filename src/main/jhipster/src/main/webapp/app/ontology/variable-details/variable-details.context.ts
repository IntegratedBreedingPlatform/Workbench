import { Injectable } from '@angular/core';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class VariableDetailsContext {
    public variableDetails = new BehaviorSubject<VariableDetails>(undefined);
}
