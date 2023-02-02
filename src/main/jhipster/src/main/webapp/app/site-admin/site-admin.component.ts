import { Component } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { HelpService } from '../shared/service/help.service';
import { HELP_SITE_ADMINISTRATION } from '../app.constants';
import { SiteAdminContext } from './site-admin-context';

declare const enableTwoFactorAuthentication: boolean;

@Component({
    selector: 'jhi-site-admin',
    templateUrl: 'site-admin.component.html',
})
export class SiteAdminComponent {

    helpLink: string;

    constructor(private context: SiteAdminContext,
                private jhiLanguageService: JhiLanguageService,
                private helpService: HelpService) {
        this.context.enableTwoFactorAuthentication = enableTwoFactorAuthentication;

        if (!this.helpLink || !this.helpLink.length) {
            helpService.getHelpLink(HELP_SITE_ADMINISTRATION).toPromise().then((response) => {
                const body = response.body;
                if (body) {
                    this.helpLink = body.data || body;
                }
            }).catch((error) => {
                console.log(error);
            });
        }
    }
}
