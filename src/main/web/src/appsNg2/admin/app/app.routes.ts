import { Routes } from '@angular/router';
import { UsersAdmin } from './users/index';
import { UserRouteAccessService } from './shared/auth/user-route-access-service';
import { RolesAdmin } from './roles/roles-admin.component';
import { SiteAdminComponent } from './site-admin.component';
import { RoleCardComponent } from './roles/role-card.component';
import { UserRoleCard } from './users/user-role.card.component';
import { UserCard } from './users/user-card.component';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/site-admin/users-admin',
        pathMatch: 'full',
        data: {
            authorities: ['ADMIN', 'ADMINISTRATION', 'SITE_ADMIN']
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'site-admin',
        component: SiteAdminComponent,
        data: {
            authorities: ['ADMIN', 'ADMINISTRATION', 'SITE_ADMIN']
        },
        canActivate: [UserRouteAccessService],
        children: [
            {
                path: 'users-admin',
                component: UsersAdmin,
                data: {
                    authorities: ['ADMIN', 'ADMINISTRATION', 'SITE_ADMIN']
                },
                canActivate: [UserRouteAccessService],
                children: [
                    {
                        path: '',
                        component: null,
                    },
                    {
                        path: 'user-card',
                        component: UserCard,
                    },
                    {
                        path: 'user-role-card',
                        component: UserRoleCard,
                    }
                ]
            },
            {
                path: 'roles-admin',
                component: RolesAdmin,
                data: {
                    authorities: ['ADMIN', 'ADMINISTRATION', 'SITE_ADMIN']
                },
                canActivate: [UserRouteAccessService],
                children: [
                    {
                        path: '',
                        component: null,
                    },
                    {
                        path: 'role-card/:id',
                        component: RoleCardComponent,
                    }
                ]
            }
        ]
    }
];
