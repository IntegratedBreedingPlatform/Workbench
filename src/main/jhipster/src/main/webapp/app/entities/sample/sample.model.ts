import { BaseEntity } from './../../shared';

export class Sample implements BaseEntity {
    constructor(
        public id?: number,
        public sampleId?: number,
        public gid?: number,
        public entryNo?: number, // sample entry number
        public designation?: string,
        public sampleName?: string,
        public sampleBusinessKey?: string,
        public takenBy?: string,
        public plantNumber?: number,
        public plantBusinessKey?: string,
        public samplingDate?: any,
        public sampleList?: string,
        public plateId?: string,
        public well?: string,
        public studyName?: string,
        public datasetType?: string,
        public observationUnitId?: string
    ) {
    }
}
