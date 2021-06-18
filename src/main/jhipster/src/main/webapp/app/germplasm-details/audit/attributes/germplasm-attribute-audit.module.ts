import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../../../shared';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmAttributeAuditModalComponent, GermplasmAttributeAuditPopupComponent } from './germplasm-attribute-audit-modal.component';
import { germplasmAttributeAuditRoutes } from './germplasm-attribute-audit.route';
import { GermplasmAttributeContext } from '../../../entities/germplasm/attribute/germplasm-attribute.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmAttributeAuditRoutes]),
    ],
    declarations: [
        GermplasmAttributeAuditModalComponent,
        GermplasmAttributeAuditPopupComponent
    ],
    entryComponents: [
        GermplasmAttributeAuditModalComponent,
        GermplasmAttributeAuditPopupComponent
    ],
    providers: [
        GermplasmAuditService,
        GermplasmAttributeContext
    ]
})
export class GermplasmAttributeAuditModule {
}
