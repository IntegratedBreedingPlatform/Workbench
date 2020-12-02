import { Routes } from '@angular/router';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
import { RouteAccessService } from '../shared';
import { GermplasmManagerComponent } from './germplasm-manager.component';
import { GermplasmSearchComponent } from './germplasm-search.component';
import { GermplasmSearchResolvePagingParams } from './germplasm-search-resolve-paging-params';
import { germplasmRoutes } from '../entities/germplasm/germplasm.route';
import { IMPORT_GERMPLASM_UPDATES_PERMISSIONS, SEARCH_GERMPLASM_PERMISSIONS } from '../shared/auth/permissions';
import { breedingMethodRoutes } from '../entities/breeding-method/breeding-method.route';
import { GermplasmSelectorComponent } from './selector/germplasm-selector.component';
import { GermplasmListCreationComponent, GermplasmListCreationPopupComponent } from './germplasm-list/germplasm-list-creation.component';
import { InventoryDetailsPopupComponent } from './inventory/details/inventory-details-modal.component';
import { inventoryDetailsRoutes } from './inventory/details/inventory-details.route';
import { GermplasmImportUpdatePopupComponent } from './germplasm-import-update-dialog.component';
import { GermplasmListAppendPopupComponent } from './germplasm-list/germplasm-list-append.component';

export const GERMPLASM_MANAGER_ROUTES: Routes = [
    ...germplasmRoutes,
    ...breedingMethodRoutes,
    ...inventoryDetailsRoutes,
    {
        path: 'lot-creation-dialog',
        component: LotCreationDialogComponent,
        data: {
            authorities: [
                'ADMIN',
                'CROP_MANAGEMENT',
                'STUDIES',
                'LISTS',
                'GERMPLASM_LISTS',
                'MG_MANAGE_INVENTORY',
                'MG_CREATE_LOTS',
                'MANAGE_STUDIES',
                'MS_CREATE_LOTS'
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
        path: 'germplasm-list-creation',
        component: GermplasmListCreationComponent,
    },
    {
        path: 'inventory-details-dialog',
        component: InventoryDetailsPopupComponent,
        outlet: 'popup',
    },
    {
        path: 'germplasm-list-append-dialog',
        component: GermplasmListAppendPopupComponent,
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
    }
]
