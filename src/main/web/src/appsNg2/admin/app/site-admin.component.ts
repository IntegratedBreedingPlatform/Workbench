import { Component } from '@angular/core';

@Component({
    selector: 'app',
    styleUrls: [
        './app.style.css'
    ],
    template: `
		<site-admin-header></site-admin-header>
		<router-outlet></router-outlet>
    `,
    moduleId: module.id,

})
export class SiteAdminComponent {

    constructor() {
    }
}

