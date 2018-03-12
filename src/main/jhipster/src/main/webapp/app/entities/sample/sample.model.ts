import { BaseEntity } from './../../shared';

export class Sample implements BaseEntity {
    constructor(
        public id?: number,
        public sampleName?: string,
        public sampleBusinessKey?: string,
        public takenBy?: string,
        public plantNumber?: number,
        public plantBusinessKey?: string,
        public samplingDate?: any,
    ) {
    }
}
