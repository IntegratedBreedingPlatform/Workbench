import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../../shared';
import { RouterModule } from '@angular/router';
import { inventoryDetailsRoutes } from './inventory-details.route';
import { InventoryDetailsComponent } from './inventory-details.component';
import { LotComponent } from './lot.component';
import { TransactionComponent } from './transaction.component';
import { InventoryDetailsModalComponent, InventoryDetailsPopupComponent } from './inventory-details-modal.component';
import { TransactionDetailsModalComponent } from './transaction-details-modal.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(inventoryDetailsRoutes)
    ],
    declarations: [
        InventoryDetailsComponent,
        InventoryDetailsModalComponent,
        InventoryDetailsPopupComponent,
        LotComponent,
        TransactionComponent,
        TransactionDetailsModalComponent
    ],
    entryComponents: [
        InventoryDetailsComponent,
        InventoryDetailsModalComponent,
        InventoryDetailsPopupComponent,
        LotComponent,
        TransactionComponent,
        TransactionDetailsModalComponent
    ],
    providers: []
})
export class InventoryDetailsModule {

}
