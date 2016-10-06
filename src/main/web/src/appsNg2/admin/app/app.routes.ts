import { RouterConfig } from '@angular/router';
import { UsersAdmin } from './users/index';

export const routes: RouterConfig = [
    {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full'
    },
    { path: 'home', component: UsersAdmin }
];
