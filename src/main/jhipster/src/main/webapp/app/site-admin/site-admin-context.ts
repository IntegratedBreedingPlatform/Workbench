import { Injectable } from '@angular/core';
import { User } from '../shared/user/model/user.model';
import { RoleType } from './model/role-type.model';
import { Role } from './model/role.model';

@Injectable()
export class SiteAdminContext {
    user: User;
    role: Role;
    enableTwoFactorAuthentication: boolean;

}
