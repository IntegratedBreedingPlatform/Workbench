import { Role } from './role.model';

export class ProgramMember {
    userId: number;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    role: Role;
}
