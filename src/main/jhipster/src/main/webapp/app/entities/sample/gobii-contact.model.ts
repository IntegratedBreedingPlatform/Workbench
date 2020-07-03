import { BaseEntity } from './../../shared';

export class GobiiContact implements BaseEntity {
    constructor(
        public id?: number,
        public firstName?: string,
        public lastName?: string
    ) {
    }
}
