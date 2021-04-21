import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { programRoutes } from './program.route';
import { ProgramComponent } from './program.component';
import { MyStudiesComponent } from './my-studies.component';
import { MyListsComponent } from './my-lists.component';
import { BarChartModule, NgxChartsModule } from '@swimlane/ngx-charts';
import { MyStudiesService } from './my-studies.service';
import { MyListsService } from './my-lists.service';
import { ProgramContext } from './program.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...programRoutes]),
        NgxChartsModule
    ],
    declarations: [
        ProgramComponent,
        MyStudiesComponent,
        MyListsComponent
    ],
    providers: [
        MyStudiesService,
        MyListsService,
        ProgramContext
    ]
})
export class ProgramModule {
}
