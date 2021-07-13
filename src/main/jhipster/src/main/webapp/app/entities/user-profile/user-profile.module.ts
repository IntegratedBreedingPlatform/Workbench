import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { USER_PROFILE_ROUTER } from './user-profile.route';
import { UserProfileUpdateDialogComponent, UserProfileUpdatePopupComponent } from './user-profile-update-dialog.component';
import { UserProfileServices } from './service/user-profile-services.service';
import { BmsjHipsterSharedModule } from '../../shared';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(USER_PROFILE_ROUTER)
    ],
    declarations: [
        UserProfileUpdateDialogComponent,
        UserProfileUpdatePopupComponent,
    ],
    entryComponents: [
        UserProfileUpdateDialogComponent,
        UserProfileUpdatePopupComponent,
    ],
    providers: [
        UserProfileServices
    ]
})
export class UserProfileModule {
}
