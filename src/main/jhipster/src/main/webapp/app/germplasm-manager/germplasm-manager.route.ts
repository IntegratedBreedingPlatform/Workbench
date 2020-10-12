import { Routes } from '@angular/router';
import { LotCreationDialogComponent } from './inventory/lot-creation-dialog.component';
import { RouteAccessService } from '../shared';
import { GermplasmManagerComponent } from './germplasm-manager.component';
import { GermplasmSearchComponent } from './germplasm-search.component';
import { GermplasmSearchResolvePagingParams } from './germplasm-search-resolve-paging-params';
import { germplasmRoutes } from '../entities/germplasm/germplasm.route';

export const GERMPLASM_MANAGER_ROUTES: Routes = [
    ...germplasmRoutes,
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
        path: 'germplasm-manager',
        component: GermplasmManagerComponent,
        data: {},
        children: [
            {
                path: '',
                redirectTo: 'germplasm-search',
                pathMatch: 'full'
            },
            {
                path: 'germplasm-search',
                component: GermplasmSearchComponent,
                data: {},
                resolve: {
                    'pagingParams': GermplasmSearchResolvePagingParams
                },
            }
        ]
    }
]
