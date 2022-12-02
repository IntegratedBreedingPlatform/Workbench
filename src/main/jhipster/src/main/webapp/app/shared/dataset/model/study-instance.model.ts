export class StudyInstanceModel {

    constructor(public instanceId: number,
                public locationName: string,
                public locationAbbreviation: string,
                public customLocationAbbreviation: string,
                public locationDescriptorDataId: number,
                public instanceNumber: number,
                public hasGeoJSON: boolean,
                public hasFieldLayout: boolean,
                public hasInventory: boolean,
                public hasExperimentalDesign: boolean,
                public hasMeasurements: boolean,
                public canBeDeleted: boolean,
                public experimentId: number) {
    }
}
