export class ObservationUnitsSearchRequest {
    constructor(public environmentDetails?: ObservationVariableDTO[],
                public environmentConditions?: ObservationVariableDTO[],
                public genericGermplasmDescriptors?: string[],
                public additionalDesignFactors?: string[],
                public datasetVariables?: ObservationVariableDTO[],
                public entryDetails?: ObservationVariableDTO[],
                public datasetId?: number,
                public instanceId?: number,
                public environmentDatasetId?: number,
                public draftMode?: boolean,
                public filterColumns?: string[],
                public draw?: string,
                public filter?: ObservationUnitsSearchFilter,
                public passportAndAttributes?: ObservationVariableDTO[],
                public nameTypes?: ObservationVariableDTO[]) {
    }
}

export class ObservationVariableDTO {
    constructor(public id?: number,
                public name?: string) {
    }
}

export class ObservationUnitsSearchFilter {
    constructor(public byOutOfBound?: boolean,
                public byOverwritten?: boolean,
                public byOutOfSync?: boolean,
                public byMissing?: boolean,
                public filteredValues?: Map<string, string[]>,
                public filteredTextValues?: Map<string, string>,
                public filteredNdExperimentIds?: number[],
                public variableTypeMap?: Map<string, string>,
                public variableId?: number,
                public variableHasValue?: boolean,
                public preFilteredGids?: number[]) {
    }
}
