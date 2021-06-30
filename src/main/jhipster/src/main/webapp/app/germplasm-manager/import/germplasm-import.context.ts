import { Injectable } from '@angular/core';
import { NameType } from '../../shared/germplasm/model/name-type.model';
import { PedigreeConnectionType } from '../../shared/germplasm/model/germplasm-import-request.model';
import { VariableDetails } from '../../shared/ontology/model/variable-details';

@Injectable()
export class GermplasmImportContext {
    // germplasm data
    data = [];
    // recover state when moving back from screens
    // [data[], data[], ...]
    dataBackup: any[][] = [];

    // data recovered from the server
    nameTypes: NameType[] = [];
    attributes: VariableDetails[] = [];

    // processed column data
    nameColumnsWithData = {};
    // data-bound to priority table -> sorted automatically by priority
    nametypesCopy: NameType[] = [];
    attributesCopy: VariableDetails[] = [];

    // inventory
    stockIdPrefix: string;
    amountConfirmed = false;

    // progenitors
    pedigreeConnectionType: PedigreeConnectionType = PedigreeConnectionType.NONE;
}
