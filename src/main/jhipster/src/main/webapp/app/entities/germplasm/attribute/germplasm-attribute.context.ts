import { Injectable } from '@angular/core';
import { GermplasmAttribute, GermplasmName } from '../../../shared/germplasm/model/germplasm.model';

@Injectable()
export class GermplasmAttributeContext {
    attributeType: string;
    attribute: GermplasmAttribute;
}
