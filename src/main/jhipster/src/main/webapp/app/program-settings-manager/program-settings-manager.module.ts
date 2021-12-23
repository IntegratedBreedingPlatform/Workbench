import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { ProgramSettingsManagerComponent } from './program-settings-manager.component';
import { BasicDetailsPaneComponent } from './tabs/basic-details/basic-details-pane.component';
import { LocationsPaneComponent } from './tabs/locations/locations-pane.component';
import { BreedingMethodsPaneComponent } from './tabs/breeding-methods/breeding-methods-pane.component';
import { MemberPaneComponent } from './tabs/members/member-pane.component';
import { PROGRAM_SETTINGS_MANAGER_ROUTES } from './program-settings-manager.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(PROGRAM_SETTINGS_MANAGER_ROUTES),
        RouterModule,
    ],
    declarations: [
        ProgramSettingsManagerComponent,
        MemberPaneComponent,
        BasicDetailsPaneComponent,
        LocationsPaneComponent,
        BreedingMethodsPaneComponent
    ],
    entryComponents: [
        ProgramSettingsManagerComponent,
        MemberPaneComponent,
        BasicDetailsPaneComponent,
        LocationsPaneComponent,
        BreedingMethodsPaneComponent
    ]
})
export class ProgramSettingsManagerModule {

}
