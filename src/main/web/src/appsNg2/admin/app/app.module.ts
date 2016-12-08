import { NgModule, CUSTOM_ELEMENTS_SCHEMA }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { EmailValidator } from './shared/validators/email-validator.component';
import { UsersDatagrid } from './users/users-datagrid.component';
import { Dialog } from './shared/components/dialog/dialog.component';
import { PaginationComponent } from './shared/components/datagrid/pagination.component';
import { UserCard } from './users/user-card.component';
import { SiteAdminHeader } from './shared/components/header/site-admin-header.component';
import { UsersAdmin } from './users/index';

import { AppComponent }  from './app.component';

@NgModule({
  imports: [BrowserModule, HttpModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  declarations: [
    AppComponent,
    EmailValidator,
    UsersDatagrid,
    Dialog,
    PaginationComponent,
    UserCard,
    SiteAdminHeader,
    UsersAdmin
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }


/*
Copyright 2016 Google Inc. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/