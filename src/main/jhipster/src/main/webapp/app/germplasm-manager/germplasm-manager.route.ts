import { Routes } from '@angular/router';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
import { RouteAccessService } from '../shared';
import { GermplasmManagerComponent } from './germplasm-manager.component';
import { GermplasmSearchComponent } from './germplasm-search.component';
import { GermplasmSearchResolvePagingParams } from './germplasm-search-resolve-paging-params';
import { IMPORT_GERMPLASM_UPDATES_PERMISSIONS, SEARCH_GERMPLASM_PERMISSIONS } from '../shared/auth/permissions';
import { breedingMethodRoutes } from '../entities/breeding-method/breeding-method.route';
import { GermplasmSelectorComponent } from './selector/germplasm-selector.component';
import { GermplasmListCreationPopupComponent } from './germplasm-list/germplasm-list-creation-popup.component';
import { GermplasmImportPopupComponent } from './import/germplasm-import.component';
import { InventoryDetailsPopupComponent } from './inventory/details/inventory-details-modal.component';
import { inventoryDetailsRoutes } from './inventory/details/inventory-details.route';
import { GermplasmImportUpdatePopupComponent } from './germplasm-import-update-dialog.component';
import { GermplasmListAddPopupComponent } from './germplasm-list/germplasm-list-add.component';
import { GermplasmSelectorPopupComponent } from './selector/germplasm-selector-modal.component';

export const GERMPLASM_MANAGER_ROUTES: Routes = [
    ...breedingMethodRoutes,
    ...inventoryDetailsRoutes,
    {
        path: 'lot-creation-dialog',
        component: LotCreationDialogComponent,
        data: {
            authorities: [
                'ADMIN',
                'STUDIES',
                'MANAGE_STUDIES',
                'MS_CREATE_LOTS',
                'LISTS',
                'GERMPLASM_LISTS',
                'MG_MANAGE_INVENTORY',
                'MG_CREATE_LOTS'
            ]
        },
        canActivate: [RouteAccessService]
    },
    {
        path: 'germplasm-selector',
        component: GermplasmSelectorComponent,
        data: { authorities: SEARCH_GERMPLASM_PERMISSIONS },
        canActivate: [RouteAccessService],
        resolve: {
            'pagingParams': GermplasmSearchResolvePagingParams
        },
    },
    {
        path: 'germplasm-selector-dialog',
        component: GermplasmSelectorPopupComponent,
        outlet: 'popup'
    },
    {
        path: 'germplasm-list-creation-dialog',
        component: GermplasmListCreationPopupComponent,
        outlet: 'popup',
    },
    {
        path: 'germplasm-import-update-dialog',
        component: GermplasmImportUpdatePopupComponent,
        data: { authorities: IMPORT_GERMPLASM_UPDATES_PERMISSIONS },
        canActivate: [RouteAccessService],
        outlet: 'popup',
    },
    {
        path: 'inventory-details-dialog',
        component: InventoryDetailsPopupComponent,
        outlet: 'popup',
    },
    {
        path: 'germplasm-list-add-dialog',
        component: GermplasmListAddPopupComponent,
        outlet: 'popup',
    },
    {
        path: 'germplasm-manager',
        component: GermplasmManagerComponent,
        data: {
            pageTitle: 'germplasm-manager.title'
        },
        children: [
            {
                path: '',
                redirectTo: 'germplasm-search',
                pathMatch: 'full'
            },
            {
                path: 'germplasm-search',
                component: GermplasmSearchComponent,
                data: { authorities: SEARCH_GERMPLASM_PERMISSIONS },
                canActivate: [RouteAccessService],
                resolve: {
                    'pagingParams': GermplasmSearchResolvePagingParams
                },
            }
        ]
    },
    {
        path: 'germplasm-import',
        component: GermplasmImportPopupComponent,
        outlet: 'popup'
    }
];
