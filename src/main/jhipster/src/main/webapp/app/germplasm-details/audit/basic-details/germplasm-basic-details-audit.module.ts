import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../../../shared';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmBasicDetailsAuditComponent } from './germplasm-basic-details-audit.component';
import { germplasmBasicDetailsAuditRoutes } from './germplasm-basic-details-audit.route';
import { BasicDetailsAuditComponent } from './basic-details-audit.component';
import { GermplasmBasicDetailsAuditModalComponent, GermplasmBasicDetailsAuditPopupComponent } from './germplasm-basic-details-audit-modal.component';
import { GermplasmDetailsContext } from '../../germplasm-details.context';
import { ReferenceAuditComponent } from './reference-audit.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmBasicDetailsAuditRoutes]),
    ],
    declarations: [
        GermplasmBasicDetailsAuditComponent,
        GermplasmBasicDetailsAuditModalComponent,
        GermplasmBasicDetailsAuditPopupComponent,
        BasicDetailsAuditComponent,
        ReferenceAuditComponent
    ],
    entryComponents: [
        GermplasmBasicDetailsAuditComponent,
        GermplasmBasicDetailsAuditModalComponent,
        GermplasmBasicDetailsAuditPopupComponent,
        BasicDetailsAuditComponent,
        ReferenceAuditComponent
    ],
    providers: [
        GermplasmAuditService,
        GermplasmDetailsContext
    ]
})
export class GermplasmBasicDetailsAuditModule {
}
