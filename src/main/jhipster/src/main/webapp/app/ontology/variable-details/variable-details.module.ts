import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { VariableDetailsComponent } from './variable-details.component';
import { variableDetailsRoutes } from './variable-details.route';
import { VariableDetailsModalComponent, VariableDetailsPopupComponent } from './variable-details-modal.component';
import { DetailsComponent } from './details.component';
import { ValidValuesComponent } from './valid-values.component';
import { VariableDetailsContext } from './variable-details.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...variableDetailsRoutes]),
    ],
    declarations: [
        VariableDetailsComponent,
        VariableDetailsPopupComponent,
        VariableDetailsModalComponent,
        DetailsComponent,
        ValidValuesComponent
    ],
    entryComponents: [
        VariableDetailsComponent,
        VariableDetailsPopupComponent,
        VariableDetailsModalComponent,
        DetailsComponent,
        ValidValuesComponent
    ],
    providers: [
        VariableDetailsContext
    ]
})
export class VariableDetailsModule {

}
