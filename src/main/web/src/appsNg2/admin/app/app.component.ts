/*
 * Angular 2 decorators and services
 */
import { Component, ViewEncapsulation } from '@angular/core';

import { TableComponent } from './demo/index';

/*
 * AppComponent Component
 * Top Level Component
 */
@Component({
  selector: 'app',
  encapsulation: ViewEncapsulation.None,
  styleUrls: [
    './app.style.css',
    './style.css'
  ],
  directives: [ TableComponent ],
  template: `
    <div class="container">
        <table-demo></table-demo>
    </div>
  `,
  moduleId: module.id

})
export class AppComponent {

  constructor() {

  }
}
