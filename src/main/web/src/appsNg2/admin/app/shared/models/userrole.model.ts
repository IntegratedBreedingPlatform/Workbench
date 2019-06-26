import { Crop } from './crop.model';
import { Program } from './program.model';

import { Role } from './role.model';

export class UserRole {

    constructor(public id: string,
                public role: Role,
                public crop: Crop,
                public program: Program) {

    }
}