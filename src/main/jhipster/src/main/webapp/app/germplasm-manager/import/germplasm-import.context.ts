import { Injectable } from '@angular/core';
import { NameType } from '../../shared/germplasm/model/name-type.model';
import { Attribute } from '../../shared/attributes/model/attribute.model';
import { PedigreeConnectionType } from '../../shared/germplasm/model/germplasm-import-request.model';

@Injectable()
export class GermplasmImportContext {
    // germplasm data
    data = [];
    // recover state when moving back from screens
    // [data[], data[], ...]
    dataBackup: any[][] = [];

    // data recovered from the server
    nameTypes: NameType[] = [];
    attributes: Attribute[] = [];

    // processed column data
    nameColumnsWithData = {};
    // data-bound to priority table -> sorted automatically by priority
    nametypesCopy: NameType[] = [];
    attributesCopy: Attribute[] = [];

    // inventory
    stockIdPrefix: string;

    // progenitors
    pedigreeConnectionType: PedigreeConnectionType = PedigreeConnectionType.NONE;
}
