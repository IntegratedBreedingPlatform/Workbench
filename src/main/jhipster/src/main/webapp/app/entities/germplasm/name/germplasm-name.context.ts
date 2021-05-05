import { Injectable } from '@angular/core';
import { GermplasmName } from '../../../shared/germplasm/model/germplasm.model';

@Injectable()
export class GermplasmNameContext {
    germplasmName: GermplasmName;
}
