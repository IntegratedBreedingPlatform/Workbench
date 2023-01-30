import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SiteAdminComponent } from './site-admin.component';
import { AccountService, BmsjHipsterSharedModule, Principal } from '../shared';
import { SITE_ADMIN_ROUTES } from './site-admin.routes';
import { RouterModule } from '@angular/router';
import { NgSelect2Module } from 'ng-select2';
import { UserRouteAccessService } from '../shared/auth/user-route-access-service';
import { EmailValidatorDirective } from './validators/email-validator.component';
import { HttpModule } from '@angular/http';
import { Select2Module } from 'ng2-select2';
import { PermissionTreeComponent, RoleEditDialogComponent, RoleEditPopupComponent } from './tabs/roles/role-edit-dialog.component';
import { UserService } from './services/user.service';
import { RoleService } from './services/role.service';
import { CropService } from './services/crop.service';
import { DedupRoleNamesPipe, UsersPaneComponent } from './tabs/users/users-pane.component';
import { RolesPaneComponent } from './tabs/roles/roles-pane.component';
import { SiteAdminContext } from './site-admin-context';
import { UserEditDialogComponent, UserEditPopupComponent, ToSelect2OptionDataPipe, ToSelect2OptionIdPipe } from './tabs/users/user-edit-dialog.component';

@NgModule({
    imports: [BmsjHipsterSharedModule,
        RouterModule.forChild(SITE_ADMIN_ROUTES),
        RouterModule,
        BrowserModule, FormsModule, CommonModule,
        HttpModule, Select2Module],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    declarations: [
        EmailValidatorDirective,
        UsersPaneComponent,
        RolesPaneComponent,
        SiteAdminComponent,
        PermissionTreeComponent,
        DedupRoleNamesPipe,
        UserEditDialogComponent,
        UserEditPopupComponent,
        RoleEditDialogComponent,
        RoleEditPopupComponent,
        ToSelect2OptionIdPipe,
        ToSelect2OptionDataPipe,
    ],
    entryComponents: [
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
        SiteAdminContext
    ]
})
export class SiteAdminModule {

}
