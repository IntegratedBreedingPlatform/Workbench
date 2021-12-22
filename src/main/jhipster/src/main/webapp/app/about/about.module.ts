import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { AboutComponent } from './about.component';
import { aboutRoutes } from './about.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(aboutRoutes)
    ],
    declarations: [
        AboutComponent
    ]
})
export class AboutModule {}
