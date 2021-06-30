import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { VariableDetailsComponent } from './variable-details.component';
import { variableDetailsRoutes } from './variable-details.route';
import { DetailsComponent } from './details.component';
import { ValidValuesComponent } from './valid-values.component';
import { VariableDetailsContext } from './variable-details.context';
import { VariableDetailsModalComponent, VariableDetailsPopupComponent } from './variable-details-modal.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...variableDetailsRoutes]),
    ],
    declarations: [
        VariableDetailsComponent,
        DetailsComponent,
        ValidValuesComponent,
        VariableDetailsModalComponent,
        VariableDetailsPopupComponent
    ],
    entryComponents: [
        VariableDetailsComponent,
        DetailsComponent,
        ValidValuesComponent,
        VariableDetailsModalComponent,
        VariableDetailsPopupComponent
    ],
    providers: [
        VariableDetailsContext
    ]
})
export class VariableDetailsModule {

}
