import { NameType } from '../../shared/germplasm/model/name-type.model';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { Injectable } from '@angular/core';

@Injectable()
export class GermplasmListImportContext {
    // germplasm List data
    data = [];

    // new variable imported
    newVariables: any[] = [];

    // existing varible
    variablesOfTheList: any[] = [];

    // discarded variables
    unknownVariableNames: any[];

    resertContext() {
        this.data = [];
        this.newVariables = [];
        this.variablesOfTheList = [];
        this.unknownVariableNames = [];
    }
}
