import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { germplasmNameChangesRoutes } from './germplasm-name-changes.route';
import { GermplasmNameChangesModalComponent, GermplasmNameChangesPopupComponent } from './germplasm-name-changes-modal.component';
import { BmsjHipsterSharedModule } from '../../shared';
import { GermplasmChangesService } from './germplasm-changes.service';
import { GermplasmNameContext } from '../../entities/germplasm/name/germplasm-name.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmNameChangesRoutes]),
    ],
    declarations: [
        GermplasmNameChangesModalComponent,
        GermplasmNameChangesPopupComponent
    ],
    entryComponents: [
        GermplasmNameChangesModalComponent,
        GermplasmNameChangesPopupComponent
    ],
    providers: [
        GermplasmChangesService,
        GermplasmNameContext
    ]
})
export class GermplasmNameChangesModule {
}
