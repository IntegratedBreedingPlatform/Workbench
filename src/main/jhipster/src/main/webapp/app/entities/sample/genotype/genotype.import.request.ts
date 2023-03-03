export class GenotypeImportRequest {
    constructor(
        public variableId?: number,
        public sampleId?: string,
        public value?: string
    ) {
    }
}