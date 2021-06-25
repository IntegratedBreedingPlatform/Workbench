import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../../../shared';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmBasicDetailsAuditComponent } from './germplasm-basic-details-audit.component';
import { germplasmBasicDetailsAuditRoutes } from './germplasm-basic-details-audit.route';
import { BasicDetailsAuditComponent } from './basic-details-audit.component';
import { GermplasmBasicDetailsAuditModalComponent, GermplasmBasicDetailsAuditPopupComponent } from './germplasm-basic-details-audit-modal.component';
import { GermplasmDetailsContext } from '../../germplasm-details.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmBasicDetailsAuditRoutes]),
    ],
    declarations: [
        GermplasmBasicDetailsAuditComponent,
        GermplasmBasicDetailsAuditModalComponent,
        GermplasmBasicDetailsAuditPopupComponent,
        BasicDetailsAuditComponent
    ],
    entryComponents: [
        GermplasmBasicDetailsAuditComponent,
        GermplasmBasicDetailsAuditModalComponent,
        GermplasmBasicDetailsAuditPopupComponent,
        BasicDetailsAuditComponent
    ],
    providers: [
        GermplasmAuditService,
        GermplasmDetailsContext
    ]
})
export class GermplasmBasicDetailsAuditModule {
}
