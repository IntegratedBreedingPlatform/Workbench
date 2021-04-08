import { Routes } from '@angular/router';
import { ProgramComponent } from './program.component';
import { MyStudiesComponent } from './my-studies.component';
import { MyListsComponent } from './my-lists.component';

export const programRoutes: Routes = [{
    path: 'programs',
    component: ProgramComponent,
    children: [{
        path: '',
        pathMatch: 'full',
        redirectTo: 'my-studies'
    }, {
        path: 'my-studies',
        component: MyStudiesComponent
    }, {
        path: 'my-lists',
        component: MyListsComponent
    }]
}]
