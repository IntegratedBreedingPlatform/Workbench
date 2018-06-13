import { Role } from './../shared/models/role.model';

export class User {

    constructor(public id: string,
                public firstName: string,
                public lastName: string,
                public username: string,
                public role: Role,
                public email: string,
                public status: string ) {
    }

}
