import { Crop } from './crop.model';
import { UserRole } from './userrole.model';

export class User {
	// transient field for easy sorting and filtering
    public roleNames: String[] = [];

    constructor(public id: string,
                public firstName: string,
                public lastName: string,
                public username: string,
                public crops: Crop[],
                public userRoles: UserRole[],
                public email: string,
                public status: string ) {
        for (let i = 0; i < this.userRoles.length; i++) {
            let userRole = this.userRoles[i];
            if (this.roleNames.indexOf(userRole.role.name) == -1) {
                this.roleNames.push(userRole.role.name)
            }
        }
    }

}
