import { Routes } from '@angular/router';
import { ProgramSettingsManagerComponent } from './program-settings-manager.component';
import { BasicDetailsPaneComponent } from './tabs/basic-details/basic-details-pane.component';
import { MemberPaneComponent } from './tabs/members/member-pane.component';
import { LocationsPaneComponent } from './tabs/locations/locations-pane.component';
import { BreedingMethodsPaneComponent } from './tabs/breeding-methods/breeding-methods-pane.component';

export const PROGRAM_SETTINGS_MANAGER_ROUTES: Routes = [
    {
        path: 'program-settings-manager',
        component: ProgramSettingsManagerComponent,
        data: {
            pageTitle: 'program-settings.title',
        },
        children: [
            {
                path: 'basic-details',
                component: BasicDetailsPaneComponent
            },
            {
                path: 'members',
                component: MemberPaneComponent
            },
            {
                path: 'locations',
                component: LocationsPaneComponent
            },
            {
                path: 'breeding-methods',
                component: BreedingMethodsPaneComponent
            },
        ]
    }
];
