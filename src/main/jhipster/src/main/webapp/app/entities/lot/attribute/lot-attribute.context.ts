import { Injectable } from '@angular/core';
import { VariableDetails } from '../../../shared/ontology/model/variable-details';
import { LotAttribute } from '../../../shared/inventory/model/lot.model';

@Injectable()
export class LotAttributeContext {
    variable: VariableDetails;
    attributeType: number;
    attribute: LotAttribute;
}
