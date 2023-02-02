import { ObservationVariable } from '../../model/observation-variable.model';
import { TreatmentVariable } from './treatment-variable.model';

export class StudyDetails {
    constructor(
        public id: number,
        public name: string,
        public description: string,
        public studyType: string,
        public objective: string,
        public createdByName: string,
        public startDate: number,
        public endDate: number,
        public lastUpdateDate: number,
        public numberOfEntries: number,
        public numberOfPlots: number,
        public hasFieldLayout: boolean,
        public numberOfVariablesWithData: number,
        public totalVariablesWithData: number,
        public studySettings: ObservationVariable[],
        public selections: ObservationVariable[],
        public entryDetails: ObservationVariable[],
        public treatmentFactors: TreatmentVariable[],
        public numberOfEnvironments: number,
        public environmentDetails: ObservationVariable[],
        public environmentConditions: ObservationVariable[],
        public experimentalDesignDetail: ExperimentalDesignDetails,
        public numberOfChecks: number,
        public nonReplicatedEntriesCount: number,
        public factorsByIds: Map<number, ObservationVariable>
    ) {
    }
}

// TODO: add missing properties
export class ExperimentalDesignDetails {
    constructor(
        public experimentalDesignDisplay: string,
        public replicationsMapDisplay: string,
        public experimentalDesign: ObservationVariable,
        public replicationPercentage?: ObservationVariable,
        public numberOfReplicates?: ObservationVariable,
        public numberOfBlocks?: ObservationVariable,
        public blockSize?: ObservationVariable,
        public numberOfRowsInReps?: ObservationVariable,
        public numberOfColsInReps?: ObservationVariable,
        public replicationsMap?: ObservationVariable,
        public numberOfRepsInCols?: ObservationVariable,
        public numberOfContiguousBlocksLatinize?: ObservationVariable,
        public numberOfContiguousRowsLatinize?: ObservationVariable,
        public numberOfContiguousColsLatinize?: ObservationVariable,
    ) {
    }
}
