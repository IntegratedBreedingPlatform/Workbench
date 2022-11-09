import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { EmailValidator } from './shared/validators/email-validator.component';
import { DedupRoleNamesPipe, UsersDatagrid } from './users/users-datagrid.component';
import { Dialog } from './shared/components/dialog/dialog.component';
import { NotificationComponent } from './shared/components/notify/notification.component';
import { PaginationComponent } from './shared/components/datagrid/pagination.component';
import { ToSelect2OptionDataPipe, ToSelect2OptionIdPipe, UserCard } from './users/user-card.component';
import { UserRoleCard } from './users/user-role.card.component';
import { SiteAdminHeader } from './shared/components/header/site-admin-header.component';
import { UsersAdmin } from './users/index';

import { AppComponent } from './app.component';
import { Select2Module } from 'ng2-select2';
import { CommonModule } from '@angular/common';
import { UserRouteAccessService } from './shared/auth/user-route-access-service';
import { Principal } from './shared/auth/principal.service';
import { AppRoutingModule } from './app-routing.module';
import { ErrorComponent } from './layouts/error/error.component';
import { AccountService } from './shared/auth/account.service';
import { RolesAdmin } from './roles/roles-admin.component';
import { SiteAdminComponent } from './site-admin.component';
import { PermissionTree, RoleCardComponent } from './roles/role-card.component';
import { RolesDatagrid } from './roles/roles-datagrid.component';
import { AppParamContext } from './shared/services/app.param.context';

@NgModule({
  imports: [BrowserModule, HttpModule, FormsModule, Select2Module, CommonModule, AppRoutingModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  declarations: [
    AppComponent,
    EmailValidator,
    UsersDatagrid,
    RolesDatagrid,
    Dialog,
    NotificationComponent,
    ErrorComponent,
    PaginationComponent,
    UserCard,
    UserRoleCard,
    SiteAdminHeader,
    SiteAdminComponent,
    UsersAdmin,
    RolesAdmin,
    RoleCardComponent,
    PermissionTree,
    ToSelect2OptionDataPipe,
    ToSelect2OptionIdPipe,
    DedupRoleNamesPipe
  ],
  providers: [
      UserRouteAccessService,
      Principal,
      AccountService,
      AppParamContext
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }


/*
Copyright 2016 Google Inc. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/
