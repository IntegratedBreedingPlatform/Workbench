import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../../shared';
import { RouterModule } from '@angular/router';
import { INVENTORY_DETAILS_ROUTES } from './inventory-details.route';
import { InventoryDetailsComponent, InventoryDetailsPopupComponent } from './inventory-details.component';
import { LotComponent } from './lot.component';
import { TransactionComponent } from './transaction.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(INVENTORY_DETAILS_ROUTES)
    ],
    declarations: [
        InventoryDetailsComponent,
        InventoryDetailsPopupComponent,
        LotComponent,
        TransactionComponent
    ],
    entryComponents: [
        InventoryDetailsComponent,
        InventoryDetailsPopupComponent,
        LotComponent,
        TransactionComponent
    ],
    providers: [
    ]
})
export class InventoryDetailsModule {

}
