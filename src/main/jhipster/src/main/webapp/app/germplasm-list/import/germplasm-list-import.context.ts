import { NameType } from '../../shared/germplasm/model/name-type.model';

export class GermplasmListImportContext {
    // germplasm List data
    data = [];

    // recover state when moving back from screens
    // [data[], data[], ...]
    dataBackup: any[][] = [];

    nametypesCopy: NameType[] = [];

}
