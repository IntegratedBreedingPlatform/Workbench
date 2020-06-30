import { BaseEntity } from '../../model/base-entity';

export class InventoryUnit implements BaseEntity{

    constructor(
        public id?: string,
        public name?: string
    ) {
    }
}
