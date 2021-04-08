import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { programRoutes } from './program.route';
import { ProgramComponent } from './program.component';
import { MyStudiesComponent } from './my-studies.component';
import { MyListsComponent } from './my-lists.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...programRoutes])
    ],
    declarations: [
        ProgramComponent,
        MyStudiesComponent,
        MyListsComponent
    ]
})
export class ProgramModule {
}
