import { Routes } from '@angular/router';
import { SiteAdminComponent } from './site-admin.component';
import { RoleEditPopupComponent } from './tabs/roles/role-edit-dialog.component';
import { UsersPaneComponent } from './tabs/users/users-pane.component';
import { RolesPaneComponent } from './tabs/roles/roles-pane.component';
import { UserEditPopupComponent } from './tabs/users/user-edit-dialog.component';
import { UserRolePopupComponent } from './tabs/users/users-role-dialog.component';
import { RouteAccessService } from '../shared';

export const SITE_ADMIN_ROUTES: Routes = [
    {
        path: 'site-admin',
        component: SiteAdminComponent,
        data: {
            pageTitle: 'site-admin.title',
            authorities: ['ADMIN', 'ADMINISTRATION', 'SITE_ADMIN']
        },
        canActivate: [RouteAccessService],
        children: [
            {
                path: '',
                redirectTo: 'users',
                pathMatch: 'full'
            },
            {
                path: 'users',
                component: UsersPaneComponent
            },
            {
                path: 'roles',
                component: RolesPaneComponent
            }
        ]
    },
    {
        path: 'role-edit-dialog',
        component: RoleEditPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'user-edit-dialog',
        component: UserEditPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'user-role-dialog',
        component: UserRolePopupComponent,
        outlet: 'popup'
    },
];
