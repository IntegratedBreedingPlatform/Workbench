import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { ViewEncapsulation } from '@angular/core';
import { HelpService } from '../shared/service/help.service';
import { HELP_MANAGE_GERMPLASM } from '../app.constants';

@Component({
    selector: 'jhi-germplasm-manager',
    templateUrl: './germplasm-manager.component.html',
})
export class GermplasmManagerComponent implements OnInit {

    helpLink: string;

    constructor(private paramContext: ParamContext, private helpService: HelpService) {
        this.paramContext.readParams();
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_MANAGE_GERMPLASM).toPromise().then((response) => {
                if (response.body) {
                    this.helpLink = response.body;
                }
            }).catch((error) => {
            });
        }
    }

    ngOnInit() {
    }

}
