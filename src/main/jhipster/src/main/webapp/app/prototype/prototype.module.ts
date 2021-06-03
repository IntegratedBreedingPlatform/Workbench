/**
 * Unfinished components, for testing purposes.
 * Clean up components after integration to avoid cluttering.
 */
import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { prototypeRoutes } from './prototype.route';
import { VariableSelectTestComponent } from './variable-select.test.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(prototypeRoutes)
    ],
    declarations: [
        VariableSelectTestComponent
    ]
})
export class PrototypeModule {

}
