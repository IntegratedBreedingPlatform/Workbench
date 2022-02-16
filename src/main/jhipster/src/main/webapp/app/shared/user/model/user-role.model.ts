import { Role } from './role.model';
import { Program } from './program.model';
import { Crop } from '../../model/crop.model';

export class UserRole {
    id: number;
    role: Role;
    crop: Crop;
    program: Program;
    createdBy: number
}
