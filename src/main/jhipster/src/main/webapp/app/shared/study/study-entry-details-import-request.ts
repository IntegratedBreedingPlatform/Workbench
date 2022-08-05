import { DatasetVariable } from './dataset-variable';

export class StudyEntryDetailsImportRequest {
    constructor(
        public programUuid?: string,
        public data?: Map<number, Map<number, string>>,
        public newVariables?: DatasetVariable[]
    ) {
    }
}
