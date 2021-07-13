import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../../../shared';
import { GermplasmNameContext } from '../../../entities/germplasm/name/germplasm-name.context';
import { GermplasmAuditService } from '../germplasm-audit.service';
import { germplasmNameAuditRoutes } from './germplasm-name-audit.route';
import { GermplasmNameAuditComponent, GermplasmNameAuditPopupComponent } from './germplasm-name-audit.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmNameAuditRoutes]),
    ],
    declarations: [
        GermplasmNameAuditComponent,
        GermplasmNameAuditPopupComponent
    ],
    entryComponents: [
        GermplasmNameAuditComponent,
        GermplasmNameAuditPopupComponent
    ],
    providers: [
        GermplasmAuditService,
        GermplasmNameContext
    ]
})
export class GermplasmNameAuditModule {
}
