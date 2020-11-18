import { Routes } from '@angular/router';
import { InventoryDetailsComponent } from './inventory-details.component';
import { LotComponent } from './lot.component';
import { RouteAccessService } from '../../../shared';

export const INVENTORY_DETAILS_ROUTES: Routes = [
    {
        path: 'inventory-details',
        component: InventoryDetailsComponent,
        canActivate: [RouteAccessService],
        children: [
            {
                path: '',
                redirectTo: 'lot',
                pathMatch: 'full'
            },
            {
                path: 'lot',
                component: LotComponent,
                // data: { authorities: SEARCH_GERMPLASM_PERMISSIONS },
                // canActivate: [RouteAccessService],
                // resolve: {
                //     'pagingParams': GermplasmSearchResolvePagingParams
                // },
            }
        ]
    }
]
