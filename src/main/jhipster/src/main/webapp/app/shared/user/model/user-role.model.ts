import { Role } from './role.model';
import { Program } from './program.model';
import { Crop } from '../../model/crop.model';

export class UserRole {
    constructor(public id?: number,
                public role?: Role,
                public crop?: Crop,
                public program?: Program,
                public createdBy?: number) {

    }

}
