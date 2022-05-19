export class Study {
    constructor(
        public active: boolean,
        public additionalInfo: any,
        public commonCropName: string,
        public contacts: Contact[],
        public culturalPractices: string,
        public dataLinks: DataLink[],
        public documentationURL: string,
        public endDate: Date,
        public environmentParameters: EnvironmentParameter[],
        public experimentalDesign: ExperimentalDesign,
        public externalReferences: ExternalReference[],
        public growthFacility: GrowthFacility,
        public lastUpdate: LastUpdate,
        public license: string,
        public locationDbId: string,
        public locationName: string,
        public observationLevels: ObservationLevel[],
        public observationUnitsDescription: string,
        public seasons: string[],
        public startDate: Date,
        public studyCode: string,
        public studyDbId: string,
        public studyDescription: string,
        public studyName: string,
        public studyPUI: string,
        public studyType: string,
        public trialDbId: string,
        public trialName: string) {
    }

}

export class Contact {
    constructor(
        public contactDbId: string,
        public email: string,
        public instituteName: string,
        public name: string,
        public orcid: string,
        public type: string) {
    }

}

export class DataLink {
    constructor(
        public dataFormat: string,
        public description: string,
        public fileFormat: string,
        public name: string,
        public provenance: string,
        public scientificType: string,
        public url: string,
        public version: string) {
    }

}

export class EnvironmentParameter {
    constructor(
        public description: string,
        public parameterName: string,
        public parameterPUI: string,
        public unit: string,
        public unitPUI: string,
        public value: string,
        public valuePUI: string) {
    }

}

export class ExperimentalDesign {
    constructor(
        public PUI: string,
        public description: string,
        public pui: string) {
    }

}

export class ExternalReference {
    constructor(
        public referenceID: string,
        public referenceSource: string) {
    }

}

export class GrowthFacility {
    constructor(
        public PUI: string,
        public description: string,
        public pui: string) {
    }

}

export class LastUpdate {
    constructor(
        public timestamp: Date,
        public version: string) {
    }
}

export class ObservationLevel {
    constructor(
        public levelName: string,
        public levelOrder: number) {
    }
}

