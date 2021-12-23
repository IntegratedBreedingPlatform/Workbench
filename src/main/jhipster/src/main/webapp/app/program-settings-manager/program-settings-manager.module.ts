import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { ProgramSettingsManagerComponent } from './program-settings-manager.component';
import { BasicDetailsPaneComponent } from './tabs/basic-details/basic-details-pane.component';
import { LocationsPaneComponent } from './tabs/locations/locations-pane.component';
import { BreedingMethodsPaneComponent } from './tabs/breeding-methods/breeding-methods-pane.component';
import { MembersPaneComponent } from './tabs/members/members-pane.component';
import { PROGRAM_SETTINGS_MANAGER_ROUTES } from './program-settings-manager.route';
import { UserTableComponent } from './tabs/members/user-table.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(PROGRAM_SETTINGS_MANAGER_ROUTES),
        RouterModule,
    ],
    declarations: [
        ProgramSettingsManagerComponent,
        MembersPaneComponent,
        UserTableComponent,
        BasicDetailsPaneComponent,
        LocationsPaneComponent,
        BreedingMethodsPaneComponent
    ],
    entryComponents: [
        ProgramSettingsManagerComponent,
        MembersPaneComponent,
        UserTableComponent,
        BasicDetailsPaneComponent,
        LocationsPaneComponent,
        BreedingMethodsPaneComponent
    ]
})
export class ProgramSettingsManagerModule {

}
