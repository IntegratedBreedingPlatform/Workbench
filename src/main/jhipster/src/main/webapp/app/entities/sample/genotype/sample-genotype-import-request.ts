export class SampleGenotypeImportRequest {
    constructor(
        public variableId?: number,
        public sampleId?: string,
        public value?: string
    ) {
    }
}