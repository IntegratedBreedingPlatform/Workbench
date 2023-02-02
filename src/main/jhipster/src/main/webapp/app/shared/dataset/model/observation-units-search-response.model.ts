import { ObservationUnitData } from './observation-unit-data.model';

export class ObservationUnitsSearchResponse {
    constructor(public observationUnitId?: number,
                public gid?: number,
                public designation?: string,
                public entryNumber?: number,
                public trialInstance?: number,
                public action?: string,
                public samplesCount?: string,
                public fileCount?: number,
                public fileVariableIds?: string[],
                public stockId?: string,
                public variables?: Map<string, ObservationUnitData>,
                public environmentVariables?: Map<string, ObservationUnitData>) {
    }
}
