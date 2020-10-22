import { Routes } from '@angular/router';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
import { RouteAccessService } from '../shared';
import { GermplasmManagerComponent } from './germplasm-manager.component';
import { GermplasmSearchComponent } from './germplasm-search.component';
import { GermplasmSearchResolvePagingParams } from './germplasm-search-resolve-paging-params';
import { germplasmRoutes } from '../entities/germplasm/germplasm.route';
import { SEARCH_GERMPLASM_PERMISSIONS } from '../shared/auth/permissions';
import { breedingMethodRoutes } from '../entities/breeding-method/breeding-method.route';
import {GermplasmSelectorComponent} from "./selector/germplasm-selector.component";

export const GERMPLASM_MANAGER_ROUTES: Routes = [
    ...germplasmRoutes,
    ...breedingMethodRoutes,
    {
        path: 'lot-creation-dialog',
        component: LotCreationDialogComponent,
        data: {
            authorities: [
                'ADMIN',
                'CROP_MANAGEMENT',
                'STUDIES',
                'MANAGE_GERMPLASM',
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
        canActivate: [RouteAccessService]
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
                canActivate: [],
                resolve: {
                    'pagingParams': GermplasmSearchResolvePagingParams
                },
            }
        ]
    }
]
