import { Injectable } from '@angular/core';
import { GermplasmAttribute, GermplasmName } from '../../../shared/germplasm/model/germplasm.model';
import { VariableDetails } from '../../../shared/ontology/model/variable-details';

@Injectable()
export class GermplasmAttributeContext {
    variable: VariableDetails;
    attributeType: number;
    attribute: GermplasmAttribute;
}
