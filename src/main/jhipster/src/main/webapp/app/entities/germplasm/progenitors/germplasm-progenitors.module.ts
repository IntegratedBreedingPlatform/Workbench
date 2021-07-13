import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../../shared';
import { RouterModule } from '@angular/router';
import { germplasmProgenitorsRoutes } from './germplasm-progenitors.route';
import { GermplasmProgenitorsModalComponent, GermplasmProgenitorsPopupComponent } from './germplasm-progenitors-modal.component';
import { GermplasmProgenitorsContext } from './germplasm-progenitors.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmProgenitorsRoutes]),
    ],
    declarations: [
        GermplasmProgenitorsModalComponent,
        GermplasmProgenitorsPopupComponent
    ],
    entryComponents: [
        GermplasmProgenitorsModalComponent,
        GermplasmProgenitorsPopupComponent
    ],
    providers: [
        GermplasmProgenitorsContext
    ]
})
export class GermplasmProgenitorsModule {
}
