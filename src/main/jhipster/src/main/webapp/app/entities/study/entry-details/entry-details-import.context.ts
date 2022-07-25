import { NameType } from '../../../shared/germplasm/model/name-type.model';
import { VariableDetails } from '../../../shared/ontology/model/variable-details';
import { Injectable } from '@angular/core';

@Injectable()
export class EntryDetailsImportContext {
    // study entries data
    data = [];

    // new variable imported
    newVariables: any[] = [];

    // existing variable
    variablesOfTheStudy: any[] = [];

    // discarded variables
    unknownVariableNames: any[];

    resetContext() {
        this.data = [];
        this.newVariables = [];
        this.variablesOfTheStudy = [];
        this.unknownVariableNames = [];
    }
}
