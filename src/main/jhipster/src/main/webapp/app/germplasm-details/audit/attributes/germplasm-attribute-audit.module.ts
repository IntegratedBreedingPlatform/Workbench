import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../../../shared';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmAttributeAuditComponent, GermplasmAttributeAuditPopupComponent } from './germplasm-attribute-audit.component';
import { germplasmAttributeAuditRoutes } from './germplasm-attribute-audit.route';
import { GermplasmAttributeContext } from '../../../entities/germplasm/attribute/germplasm-attribute.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmAttributeAuditRoutes]),
    ],
    declarations: [
        GermplasmAttributeAuditComponent,
        GermplasmAttributeAuditPopupComponent
    ],
    entryComponents: [
        GermplasmAttributeAuditComponent,
        GermplasmAttributeAuditPopupComponent
    ],
    providers: [
        GermplasmAuditService,
        GermplasmAttributeContext
    ]
})
export class GermplasmAttributeAuditModule {
}
