import { RouterConfig } from '@angular/router';
import { TableComponent } from './users/index';

export const routes: RouterConfig = [
    {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full'
    },
    { path: 'home', component: TableComponent }
];
