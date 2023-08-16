import {NgModule} from '@angular/core';
import {BmsjHipsterSharedModule} from '../shared';
import {RouterModule} from '@angular/router';
import {CROSS_PLAN_MANAGER_ROUTES} from './cross-plan-list.route';
import {CrossPlanListComponent} from './cross-plan-list.component';
import {CrossPlanListSearchComponent} from './cross-plan-list-search.component';
import {CrossPlanDesignComponent} from './cross-plan-design.component';
import {GermplasmManagerModule} from '../germplasm-manager/germplasm-manager.module';
import { ListParentsContext } from './list-parents-context';
import {ParentListComponent} from "./parent-list.component";
import {PreviewCrossesComponent} from "./preview-crosses.component";
import {ParentListColumnsComponent} from "./parent-list-columns.component";

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(CROSS_PLAN_MANAGER_ROUTES),
        GermplasmManagerModule,
    ],
    declarations: [
        CrossPlanListComponent,
        CrossPlanListSearchComponent,
        CrossPlanDesignComponent,
        ParentListComponent,
        PreviewCrossesComponent,
        ParentListColumnsComponent
    ],
    entryComponents: [
        CrossPlanListComponent,
        CrossPlanListSearchComponent,
        CrossPlanDesignComponent,
        ParentListComponent,
        PreviewCrossesComponent,
        ParentListColumnsComponent
    ],
    providers: [
        ListParentsContext
    ]
})

export class CrossPlanModule {

}
