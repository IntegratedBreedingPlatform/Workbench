import { BaseEntity } from './../../shared';
import { Sample } from './sample.model';

export class SampleList implements BaseEntity {
    constructor(
        public id?: number,
        public listName?: string,
        public description?: string,
        public active?: boolean,
        public gobiiProjectId?: number,
        public samples?: Sample[]
    ) {
    }
}
