import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { lotAttributeRoutes } from './lot-attribute.route';
import { LotAttributeContext } from './lot-attribute.context';
import { LotAttributeModalComponent, LotAttributePopupComponent } from './lot-attribute-modal.component';
import { BmsjHipsterSharedModule } from '../../../shared';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...lotAttributeRoutes]),
    ],
    declarations: [
        LotAttributeModalComponent,
        LotAttributePopupComponent
    ],
    entryComponents: [
        LotAttributeModalComponent,
        LotAttributePopupComponent
    ],
    providers: [
        LotAttributeContext
    ]
})
export class LotAttributeModule {
}
