import {Routes} from "@angular/router";
import {CrossPlanListComponent} from "./cross-plan-list.component";
import {CrossPlanDesignComponent} from "./cross-plan-design.component";


export const CROSS_PLAN_MANAGER_ROUTES: Routes = [
    {
        path: "cross-plan-manager",
        component: CrossPlanListComponent,
        children: [
            {
                path: '',
                redirectTo: 'cross-plan-lists-search',
                pathMatch: 'full'
            }
        ]
    },
    {
        path: "cross-plan-design",
        component: CrossPlanDesignComponent
    }
]