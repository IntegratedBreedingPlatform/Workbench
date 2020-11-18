import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../../shared';
import { RouterModule } from '@angular/router';
import { INVENTORY_DETAILS_ROUTES } from './inventory-details.route';
import { InventoryDetailsComponent } from './inventory-details.component';
import { LotComponent } from './lot.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(INVENTORY_DETAILS_ROUTES)
    ],
    declarations: [
        InventoryDetailsComponent,
        LotComponent
    ],
    entryComponents: [
        InventoryDetailsComponent,
        LotComponent
    ],
    providers: [
    ]
})
export class InventoryDetailsModule {

}
