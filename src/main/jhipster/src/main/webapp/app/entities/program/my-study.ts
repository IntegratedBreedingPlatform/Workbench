export class MyStudy {
    constructor(
        public studyId: number,
        public name: string,
        public type: string,
        public date: string,
        public folder: string,
        public selected?: boolean,
        public metadata?: MyStudyMetadata
    ) {
    }
}

export class MyStudyMetadata {
    constructor(
        public observations?: (ObservationsMetadata| NgChartsBarPlotMetadata)[],
        public hasMoreEnvironments?: boolean
    ) {
    }
}

export class ObservationsMetadata {
    constructor(
        public studyId: number,
        public datasetName: string,
        public instanceName: string,
        public confirmedCount: number,
        public pendingCount: number,
        public unobservedCount: number,
    ) {
    }
}

export class NgChartsBarPlotMetadata {
    constructor(
        public name: string,
        public series: { name: string, value: number }[]
    ) {
    }
}
