/// <reference path="../../../../typings/globals/node/index.d.ts" />

/*
 * Angular 2 decorators and services
 */
import { Component, ViewEncapsulation } from '@angular/core';

import { TableComponent } from './users/index';
import { UserService } from './users/user.service';

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
  directives: [ TableComponent ],
  template: `
    <div class="container">
        <users-table></users-table>
    </div>
  `,
  moduleId: module.id,
  providers: [UserService]

})
export class AppComponent {

  constructor() {

  }
}
