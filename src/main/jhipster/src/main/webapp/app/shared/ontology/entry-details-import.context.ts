import { Injectable } from '@angular/core';

@Injectable()
export class EntryDetailsImportContext {
    // current entries data
    data = [];

    // new variable imported
    newVariables: any[] = [];

    // existing variable
    variablesOfTheList: any[] = [];

    // discarded variables
    unknownVariableNames: any[];

    variableMatchesResult: any = {};

    skipVariables: any[] = [];

    resetContext() {
        this.data = [];
        this.newVariables = [];
        this.variablesOfTheList = [];
        this.unknownVariableNames = [];
        this.skipVariables = [];
    }
}
