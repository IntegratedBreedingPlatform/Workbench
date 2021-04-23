import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../../shared';
import { RouterModule } from '@angular/router';
import { germplasmAttributeRoutes } from './germplasm-attribute.route';
import { GermplasmAttributeContext } from './germplasm-attribute.context';
import { GermplasmAttributeModalComponent, GermplasmAttributePopupComponent } from './germplasm-attribute-modal.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmAttributeRoutes]),
    ],
    declarations: [
        GermplasmAttributeModalComponent,
        GermplasmAttributePopupComponent
    ],
    entryComponents: [
        GermplasmAttributeModalComponent,
        GermplasmAttributePopupComponent
    ],
    providers: [
        GermplasmAttributeContext
    ]
})
export class GermplasmAttributeModule {
}
