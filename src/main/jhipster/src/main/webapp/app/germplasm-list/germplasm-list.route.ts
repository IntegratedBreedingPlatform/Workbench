import { Routes } from '@angular/router';
import { RouteAccessService } from '../shared';
import { MANAGE_GERMPLASM_LIST_PERMISSIONS } from '../shared/auth/permissions';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { GermplasmListComponent } from './germplasm-list.component';
import { ListComponent } from './list.component';
import { GermplasmListImportPopupComponent } from './import/germplasm-list-import.component';

export const GERMPLASM_LIST_ROUTES: Routes = [
    {
        path: 'germplasm-list',
        component: GermplasmListComponent,
        children: [
            {
                path: '',
                redirectTo: 'germplasm-lists-search',
                pathMatch: 'full'
            },
            {
                path: 'germplasm-lists-search',
                component: GermplasmListSearchComponent,
                data: { authorities: [...MANAGE_GERMPLASM_LIST_PERMISSIONS,'SEARCH_GERMPLASM_LISTS'] },
                canActivate: [RouteAccessService]
            },
            {
                path: 'list/:listId',
                component: ListComponent,
                data: { authorities: [...MANAGE_GERMPLASM_LIST_PERMISSIONS,'SEARCH_GERMPLASM_LISTS'] },
                canActivate: [RouteAccessService]
            }
        ]
    },
    {
        path: 'import-germplasm-list',
        component: GermplasmListImportPopupComponent,
        outlet: 'popup',
        data: { authorities: [...MANAGE_GERMPLASM_LIST_PERMISSIONS,'SEARCH_GERMPLASM_LISTS', 'IMPORT_GERMPLASM_LISTS'] },
        canActivate: [RouteAccessService]
    }
];
