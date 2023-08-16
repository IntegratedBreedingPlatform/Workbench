import {Routes} from '@angular/router';
import {CrossPlanManagerComponent} from './cross-plan-manager.component';
import {CrossPlanDesignComponent} from './cross-plan-design.component';
import {ParentListComponent} from './parent-list.component';

export const CROSS_PLAN_MANAGER_ROUTES: Routes = [
    {
        path: 'cross-plan-manager',
        component: CrossPlanManagerComponent,
        children: [
            {
                path: '',
                redirectTo: 'cross-plan-lists-search',
                pathMatch: 'full'
            }
        ]
    },
    {
        path: 'cross-plan-design',
        component: CrossPlanDesignComponent
    },
    {
        path: 'parent-list',
        component: ParentListComponent
    }
]
