import { Injectable } from '@angular/core';
import { GermplasmAttribute } from '../../../shared/germplasm/model/germplasm.model';
import { GermplasmAttributeType } from './germplasm-attribute-type';

@Injectable()
export class GermplasmAttributeContext {
    attributeType: GermplasmAttributeType;
    attribute: GermplasmAttribute;
}
