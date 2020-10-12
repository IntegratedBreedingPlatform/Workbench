import { NgModule } from '@angular/core';
import { GermplasmComponent, GermplasmPopupComponent } from './germplasm.component';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { germplasmRoutes } from './germplasm.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...germplasmRoutes]),
    ],
    declarations: [
        GermplasmComponent,
        GermplasmPopupComponent
    ],
    entryComponents: [
        GermplasmComponent,
        GermplasmPopupComponent
    ]
})
export class GermplasmModule {

}
