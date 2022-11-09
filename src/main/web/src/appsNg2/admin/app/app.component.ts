/// <reference path="../../../../typings/globals/node/index.d.ts" />

/*
 * Angular 2 decorators and services
 */
import { Component, ViewEncapsulation } from '@angular/core';
import { UserService } from './shared/services/user.service';
import { RoleService } from './shared/services/role.service';
import { MailService } from './shared/services/mail.service';
import { CropService } from './shared/services/crop.service';
import { HelpService } from './shared/services/help.service';
import { AppParamContext } from './shared/services/app.param.context';


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
			<router-outlet></router-outlet>
		</div>
    `,
    moduleId: module.id,
    providers: [UserService, RoleService, MailService, CropService, HelpService]

})
export class AppComponent {

    constructor(private appParamContext: AppParamContext) {
        this.appParamContext.enableTwoFactorAuthentication = (<any>window).enableTwoFactorAuthentication;
    }
}
