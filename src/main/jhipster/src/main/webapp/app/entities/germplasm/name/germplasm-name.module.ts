import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../../shared';
import { GermplasmNameModalComponent, GermplasmNamePopupComponent } from './germplasm-name-modal.component';
import { RouterModule } from '@angular/router';
import { germplasmNameRoutes } from './germplasm-name.route';
import { GermplasmNameContext } from './germplasm-name.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmNameRoutes]),
    ],
    declarations: [
        GermplasmNameModalComponent,
        GermplasmNamePopupComponent
    ],
    entryComponents: [
        GermplasmNameModalComponent,
        GermplasmNamePopupComponent
    ],
    providers: [
        GermplasmNameContext
    ]
})
export class GermplasmNameModule {
}
