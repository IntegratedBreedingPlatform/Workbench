import { BaseEntity } from '../shared';

export class GermplasmList implements BaseEntity {
    constructor(
        public id: number,
        public listName: string,
        public description: string,
        public active: boolean
    ) {
    }
}
