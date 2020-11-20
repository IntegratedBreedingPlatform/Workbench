import { Routes } from '@angular/router';
import { InventoryDetailsComponent } from './inventory-details.component';
import { LotComponent } from './lot.component';
import { RouteAccessService } from '../../../shared';
import { TransactionComponent } from './transaction.component';

export const inventoryDetailsRoutes: Routes = [
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
                component: LotComponent
            },
            {
                path: 'transaction',
                component: TransactionComponent
            }
        ]
    }
]
