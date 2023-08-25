import {NgModule} from '@angular/core';
import {BmsjHipsterSharedModule} from '../shared';
import {RouterModule} from '@angular/router';
import {CROSS_PLAN_MANAGER_ROUTES} from './cross-plan-manager.route';
import {CrossPlanManagerComponent} from './cross-plan-manager.component';
import {CrossPlanListSearchComponent} from './cross-plan-list-search.component';
import {CrossPlanDesignComponent} from './cross-plan-design.component';
import {GermplasmManagerModule} from '../germplasm-manager/germplasm-manager.module';
import {ParentListComponent} from './parent-list.component';
import {PreviewCrossesComponent} from './preview-crosses.component';
import {ParentListColumnsComponent} from './parent-list-columns.component';
import {CrossPlanDesignService} from '../shared/cross-plan-design/service/cross-plan-design.service';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(CROSS_PLAN_MANAGER_ROUTES),
        GermplasmManagerModule,
    ],
    declarations: [
        CrossPlanManagerComponent,
        CrossPlanListSearchComponent,
        CrossPlanDesignComponent,
        ParentListComponent,
        PreviewCrossesComponent,
        ParentListColumnsComponent
    ],
    entryComponents: [
        CrossPlanManagerComponent,
        CrossPlanListSearchComponent,
        CrossPlanDesignComponent,
        ParentListComponent,
        PreviewCrossesComponent,
        ParentListColumnsComponent
    ],
    providers: [
        CrossPlanDesignService
    ]
})

export class CrossPlanManagerModule {

}
