import { NameType } from '../../shared/germplasm/model/name-type.model';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { Injectable } from '@angular/core';

@Injectable()
export class GermplasmListImportContext {
    // germplasm List data
    data = [];

    // recover state when moving back from screens
    // [data[], data[], ...]
    dataBackup: any[][] = [];

    // new variable imported
    newVariables: any[] = [];

    // existing varible
    variablesOfTheList: any[] = [];

    // discarded variables
    unknownVariableNames: any[];
}
