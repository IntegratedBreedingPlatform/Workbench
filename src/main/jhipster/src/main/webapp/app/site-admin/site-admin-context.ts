import { Injectable } from '@angular/core';
import { User } from '../shared/user/model/user.model';
import { RoleType } from './models/role-type.model';
import { Role } from './models/role.model';

@Injectable()
export class SiteAdminContext {
    user: User;
    role: Role;
    enableTwoFactorAuthentication: boolean;

}
