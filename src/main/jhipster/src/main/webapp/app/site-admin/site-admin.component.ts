import { Component } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { HelpService } from '../shared/service/help.service';
import { HELP_SITE_ADMINISTRATION } from '../app.constants';
import { SiteAdminContext } from './site-admin-context';

@Component({
    selector: 'jhi-site-admin',
    templateUrl: 'site-admin.component.html',
})
export class SiteAdminComponent {

    helpLink: string;

    constructor(private context: SiteAdminContext,
                private jhiLanguageService: JhiLanguageService,
                private helpService: HelpService) {
        this.context.enableTwoFactorAuthentication = (<any>window).enableTwoFactorAuthentication;

        if (!this.helpLink || !this.helpLink.length) {
            helpService.getHelpLink(HELP_SITE_ADMINISTRATION).toPromise().then((response) => {
                const body = response.text();
                console.log(body);
                if (body) {
                    this.helpLink = body.data || body;
                }
                console.log(this.helpLink);
            }).catch((error) => {
                console.log(error);
            });
        }
    }
}
