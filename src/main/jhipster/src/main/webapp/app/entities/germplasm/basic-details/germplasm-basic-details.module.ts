import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../../shared';
import { RouterModule } from '@angular/router';
import { germplasmBasicDetailsRoute } from './germplasm-basic-details.route';
import { EditGermplasmBasicDetailsPopupComponent, GermplasmBasicDetailsModalComponent } from './germplasm-basic-details-modal.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmBasicDetailsRoute]),
    ],
    declarations: [
        GermplasmBasicDetailsModalComponent,
        EditGermplasmBasicDetailsPopupComponent
    ],
    entryComponents: [
        GermplasmBasicDetailsModalComponent,
        EditGermplasmBasicDetailsPopupComponent
    ]
})
export class GermplasmBasicDetailsModule {

}
