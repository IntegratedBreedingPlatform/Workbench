import { Routes } from '@angular/router';
import { UserProfileUpdatePopupComponent } from './user-profile-update-dialog.component';

export const USER_PROFILE_ROUTER: Routes = [
    {
        path: 'user-profile-update',
        component: UserProfileUpdatePopupComponent,
        outlet: 'popup'
    }
];
