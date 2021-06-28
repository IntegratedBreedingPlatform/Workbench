import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../../../shared';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { GermplasmProgenitorsAuditComponent } from './germplasm-progenitors-audit.component';
import { germplasmProgenitorDetailsAuditRoutes } from './germplasm-progenitors-audit.route';
import { DetailsAuditComponent } from './details-audit.component';
import { GermplasmBasicDetailsAuditPopupComponent, GermplasmProgenitorsAuditModalComponent } from './germplasm-progenitors-audit-modal.component';
import { GermplasmDetailsContext } from '../../germplasm-details.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmProgenitorDetailsAuditRoutes]),
    ],
    declarations: [
        GermplasmProgenitorsAuditComponent,
        GermplasmProgenitorsAuditModalComponent,
        GermplasmBasicDetailsAuditPopupComponent,
        DetailsAuditComponent,
        // ProgenitorsAuditComponent
    ],
    entryComponents: [
        GermplasmProgenitorsAuditComponent,
        GermplasmProgenitorsAuditModalComponent,
        GermplasmBasicDetailsAuditPopupComponent,
        DetailsAuditComponent,
        // ProgenitorsAuditComponent
    ],
    providers: [
        GermplasmAuditService,
        GermplasmDetailsContext
    ]
})
export class GermplasmProgenitorsAuditModule {
}
