import { Injectable } from '@angular/core';
import { NameType } from '../../shared/germplasm/model/name-type.model';
import { Attribute } from '../../shared/attributes/model/attribute.model';

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
    nametypesCopy: NameType[] = [];
    attributesCopy: Attribute[] = [];

}
