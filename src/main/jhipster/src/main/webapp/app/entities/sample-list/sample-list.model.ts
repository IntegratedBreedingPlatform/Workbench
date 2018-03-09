import { BaseEntity } from './../../shared';

export class SampleList implements BaseEntity {
    constructor(
        public id?: number,
        public listName?: string,
        public description?: string,
        public hierarchy?: number,
        public createdDate?: any,
        public notes?: string,
        public type?: string,
    ) {
    }
}
