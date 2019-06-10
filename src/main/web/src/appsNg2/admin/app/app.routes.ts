import { Routes } from '@angular/router';
import { UsersAdmin } from './users/index';
import { UserRouteAccessService } from './shared/auth/user-route-access-service';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/users-admin',
        pathMatch: 'full',
        data: {
            authorities: ['SUPERADMIN', 'ADMIN', 'ADMINISTRATION', 'SITE_ADMIN']
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'users-admin',
        component: UsersAdmin,
        data: {
            authorities: ['SUPERADMIN', 'ADMIN', 'ADMINISTRATION', 'SITE_ADMIN']
        },
        canActivate: [UserRouteAccessService]
    }
];
