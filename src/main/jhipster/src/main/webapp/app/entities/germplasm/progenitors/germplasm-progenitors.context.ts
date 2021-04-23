import { Injectable } from '@angular/core';
import { GermplasmProgenitorsDetails } from '../../../shared/germplasm/model/germplasm.model';

@Injectable()
export class GermplasmProgenitorsContext {
    germplasmProgenitorsDetails: GermplasmProgenitorsDetails
}
