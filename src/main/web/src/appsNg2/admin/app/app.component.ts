/// <reference path="../../../../typings/globals/node/index.d.ts" />

/*
 * Angular 2 decorators and services
 */
import { Component, ViewEncapsulation } from '@angular/core';

import { UsersAdmin } from './users/index';
import { SiteAdminHeader } from './shared/components/header/site-admin-header.component';
import { UserService } from './shared/services/user.service';
import { RoleService } from './shared/services/role.service';
import { MailService } from './shared/services/mail.service';


/*
 * AppComponent Component
 * Top Level Component
 */
@Component({
  selector: 'app',
  encapsulation: ViewEncapsulation.None,
  styleUrls: [
    './app.style.css'
  ],
  template: `
    <div class="container-fluid">
        <site-admin-header></site-admin-header>
        <users-admin></users-admin>
    </div>
  `,
  moduleId: module.id,
  providers: [UserService, RoleService, MailService]

})
export class AppComponent {

  constructor() {

  }
}
