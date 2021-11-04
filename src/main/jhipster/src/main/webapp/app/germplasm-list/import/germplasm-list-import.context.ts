import { NameType } from '../../shared/germplasm/model/name-type.model';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { Injectable } from '@angular/core';

@Injectable()
export class GermplasmListImportContext {
    // germplasm List data
    data = [];

    // new variable imported
    newVariables: any[] = [];

    // existing variable
    variablesOfTheList: any[] = [];

    // discarded variables
    unknownVariableNames: any[];

    resetContext() {
        this.data = [];
        this.newVariables = [];
        this.variablesOfTheList = [];
        this.unknownVariableNames = [];
    }
}
