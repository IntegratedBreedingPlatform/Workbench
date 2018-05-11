import { BaseEntity } from './../../shared';
import { Sample } from './sample.model';

export class SampleList implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public active?: boolean,
        public samples?: Sample[]
    ) {
    }
}
