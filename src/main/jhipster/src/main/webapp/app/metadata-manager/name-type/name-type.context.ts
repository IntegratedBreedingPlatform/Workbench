import { Injectable } from '@angular/core';
import { NameTypeDetails } from '../../shared/germplasm/model/name-type.model';

@Injectable()
export class NameTypeContext {
    nameTypeDetails: NameTypeDetails;
}
