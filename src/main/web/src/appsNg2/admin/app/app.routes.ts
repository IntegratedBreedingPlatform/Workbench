import { Routes } from '@angular/router';
import { UsersAdmin } from './users/index';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full'
    },
    { path: 'home', component: UsersAdmin }
];
