import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { lotAttributeRoutes } from './lot-attribute.route';
import { LotAttributeContext } from './attribute/lot-attribute.context';
import { LotAttributeModalComponent, LotAttributePopupComponent } from './attribute/lot-attribute-modal.component';
import { BmsjHipsterSharedModule } from '../../shared';
import { LotAttributesPaneComponent } from './lot-attributes-pane.component';
import { LotService } from '../../shared/inventory/service/lot.service';
import { LotDetailContext } from './lot-detail.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...lotAttributeRoutes]),
    ],
    declarations: [
        LotAttributeModalComponent,
        LotAttributePopupComponent,
        LotAttributesPaneComponent
    ],
    entryComponents: [
        LotAttributeModalComponent,
        LotAttributePopupComponent,
        LotAttributesPaneComponent
    ],
    providers: [
        LotAttributeContext,
        LotService,
        LotDetailContext
    ]
})
export class LotAttributeModule {
}
