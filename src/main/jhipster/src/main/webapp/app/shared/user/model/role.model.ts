import { RoleTypeEnum } from './role-type.enum';

export class Role {

    id?: number;
    name?: string;
    description?: string;
    roleType?: RoleTypeEnum;
    active?: boolean;
    editable?: boolean;
    assignable?: boolean;
}
