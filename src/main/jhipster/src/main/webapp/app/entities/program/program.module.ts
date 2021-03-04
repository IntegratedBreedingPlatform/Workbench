import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { programRoutes } from './program.route';
import { ProgramComponent } from './program.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...programRoutes])
    ],
    declarations: [
        ProgramComponent
    ]
})
export class ProgramModule {
}
