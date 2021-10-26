import { NameType } from '../../shared/germplasm/model/name-type.model';
import { VariableDetails } from '../../shared/ontology/model/variable-details';

export class GermplasmListImportContext {
    // germplasm List data
    data = [];

    // recover state when moving back from screens
    // [data[], data[], ...]
    dataBackup: any[][] = [];

    entryDetails: VariableDetails[] = [];
    unknownNameVariables: any[];
}
