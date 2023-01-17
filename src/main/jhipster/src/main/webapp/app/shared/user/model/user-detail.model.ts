import { Crop } from '../../model/crop.model';
import { UserRole } from './user-role.model';

export class UserDetail implements Account {
    /*
     * required members from Account interface
     * just to indicate that this class is related to the one used in principal and account service
     */
    id: string;
    displayName: string;
    rpDisplayName: string;

    // from UserDetailDto (backend)
    username: string;
    firstName: string;
    lastName: string;
    userRoles: UserRole[];
    active: boolean;
    email: string;
    crops: Crop[];
    authorities: string[];
}
