import { Routes } from '@angular/router';
import { RouteAccessService } from '../shared';
import { MANAGE_GERMPLASM_LIST_PERMISSION, SEARCH_GERMPLASM_LISTS_PERMISSION } from '../shared/auth/permissions';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { GermplasmListComponent } from './germplasm-list.component';
import { ListComponent } from './list.component';
import { GermplasmListImportPopupComponent } from './import/germplasm-list-import.component';
import { GermplasmListImportUpdatePopupComponent } from './import/germplasm-list-import-update.component';
import { GermplasmListClonePopupComponent } from './germplasm-list-clone-popup.component';

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
                data: { authorities: [...SEARCH_GERMPLASM_LISTS_PERMISSION] },
                canActivate: [RouteAccessService]
            },
            {
                /**
                 * :listId param is to track active link, but listId queryParam is actually used to open tabs
                 * because it's also available to parent (and all) component
                 */
                path: 'list/:listId',
                component: ListComponent,
                data: { authorities: [...SEARCH_GERMPLASM_LISTS_PERMISSION] },
                canActivate: [RouteAccessService]
            }
        ]
    },
    {
        path: 'germplasm-list-import',
        component: GermplasmListImportPopupComponent,
        outlet: 'popup',
        data: { authorities: [...SEARCH_GERMPLASM_LISTS_PERMISSION, 'IMPORT_GERMPLASM_LISTS'] },
        canActivate: [RouteAccessService]
    },
    {
        path: 'germplasm-list-import-update',
        component: GermplasmListImportUpdatePopupComponent,
        outlet: 'popup',
        data: { authorities: [...SEARCH_GERMPLASM_LISTS_PERMISSION, 'IMPORT_GERMPLASM_LIST_UPDATES'] },
        canActivate: [RouteAccessService]
    },
    {
        path: 'germplasm-list-clone-dialog',
        component: GermplasmListClonePopupComponent,
        outlet: 'popup',
    }
];
