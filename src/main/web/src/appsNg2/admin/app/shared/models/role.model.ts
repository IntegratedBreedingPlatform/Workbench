import { Permission } from './permission.model';
import { RoleType } from './role-type.model';

export class Role {

    constructor(public id?: number,
                public name?: string,
                public description?: string,
                public type?: string, // FIXME Deprecated
                public roleType?: RoleType,
                public active?: boolean,
                public editable?: boolean,
                public assignable?: boolean,
                public permissions?: Permission[]) {
    }
}