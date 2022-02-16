import { Routes } from '@angular/router';
import { ProgramSettingsManagerComponent } from './program-settings-manager.component';
import { BasicDetailsPaneComponent } from './tabs/basic-details/basic-details-pane.component';
import { MembersPaneComponent } from './tabs/members/members-pane.component';
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
                path: '',
                redirectTo: 'basic-details',
                pathMatch: 'full'
            },
            {
                path: 'basic-details',
                component: BasicDetailsPaneComponent
            },
            {
                path: 'members',
                component: MembersPaneComponent
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
