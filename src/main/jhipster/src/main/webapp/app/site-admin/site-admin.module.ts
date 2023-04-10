import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SiteAdminComponent } from './site-admin.component';
import { AccountService, BmsjHipsterSharedModule, Principal } from '../shared';
import { SITE_ADMIN_ROUTES } from './site-admin.routes';
import { RouterModule } from '@angular/router';
import { UserRouteAccessService } from '../shared/auth/user-route-access-service';
import { EmailValidatorDirective } from './validators/email-validator.component';
import { PermissionTreeComponent, RoleEditDialogComponent, RoleEditPopupComponent } from './tabs/roles/role-edit-dialog.component';
import { UserService } from './services/user.service';
import { RoleService } from './services/role.service';
import { CropService } from './services/crop.service';
import { DedupRoleNamesPipe, UsersPaneComponent } from './tabs/users/users-pane.component';
import { RolesPaneComponent } from './tabs/roles/roles-pane.component';
import { SiteAdminContext } from './site-admin-context';
import { ToSelect2OptionDataPipe, ToSelect2OptionIdPipe, UserEditDialogComponent, UserEditPopupComponent } from './tabs/users/user-edit-dialog.component';
import { UserRoleDialogComponent, UserRolePopupComponent } from './tabs/users/users-role-dialog.component';
import { MailService } from './services/mail.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
    imports: [BmsjHipsterSharedModule,
        RouterModule.forChild(SITE_ADMIN_ROUTES),
        RouterModule,
        BrowserModule, FormsModule, CommonModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    declarations: [
        EmailValidatorDirective,
        UsersPaneComponent,
        RolesPaneComponent,
        SiteAdminComponent,
        PermissionTreeComponent,
        UserRoleDialogComponent,
        UserRolePopupComponent,
        UserEditDialogComponent,
        UserEditPopupComponent,
        RoleEditDialogComponent,
        RoleEditPopupComponent,
        ToSelect2OptionIdPipe,
        ToSelect2OptionDataPipe,
        DedupRoleNamesPipe,
    ],
    entryComponents: [
        UserRoleDialogComponent,
        UserRolePopupComponent,
        UserEditDialogComponent,
        UserEditPopupComponent,
        RoleEditDialogComponent,
        RoleEditPopupComponent,
        PermissionTreeComponent
    ],
    providers: [
        UserRouteAccessService,
        UserService,
        RoleService,
        CropService,
        Principal,
        AccountService,
        MailService,
        SiteAdminContext
    ]
})
export class SiteAdminModule {

}
