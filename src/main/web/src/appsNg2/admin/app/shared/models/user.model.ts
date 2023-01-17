import { Crop } from './crop.model';
import { UserRole } from './user-role.model';

export class User {
    constructor(public id: string,
                public firstName: string,
                public lastName: string,
                public username: string,
                public crops: Crop[],
                public userRoles: UserRole[],
                public email: string,
                public active: boolean,
                public multiFactorAuthenticationEnabled: boolean) {
    }

}
