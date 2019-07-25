import { Permission } from './permission.model';

export class Role {

    constructor(public id?: number,
                public name?: string,
                public description?: string,
                public type?: string,
                public active?: string,
                public editable?: string,
                public assignable?: string,
                public permissions?: Permission[]) {
    }
}