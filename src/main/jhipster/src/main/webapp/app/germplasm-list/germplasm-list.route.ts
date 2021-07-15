import { Routes } from '@angular/router';
import { RouteAccessService } from '../shared';
import { SEARCH_GERMPLASM_LIST_PERMISSIONS } from '../shared/auth/permissions';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { GermplasmListComponent } from './germplasm-list.component';

export const GERMPLASM_LIST_ROUTES: Routes = [
    {
        path: 'germplasm-list',
        component: GermplasmListComponent,
        children: [
            {
                path: '',
                redirectTo: 'germplasm-list-search',
                pathMatch: 'full'
            },
            {
                path: 'germplasm-list-search',
                component: GermplasmListSearchComponent,
                data: { authorities: SEARCH_GERMPLASM_LIST_PERMISSIONS },
                canActivate: [RouteAccessService]
            }
        ]
    }
];
