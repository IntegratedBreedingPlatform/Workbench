import { Role } from './../shared/models/role.model';

export class User {
	// transient field for easy sorting and filtering
	public roleName : String = "";

    constructor(public id: string,
                public firstName: string,
                public lastName: string,
                public username: string,
                public role: Role,
                public email: string,
                public status: string ) {
        if (role.description) {
	        this.roleName = role.description;
        }
    }

}
