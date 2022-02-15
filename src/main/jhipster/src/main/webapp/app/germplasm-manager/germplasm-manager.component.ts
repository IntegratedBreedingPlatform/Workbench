import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_MANAGE_GERMPLASM } from '../app.constants';
import { ListBuilderContext } from '../shared/list-builder/list-builder.context';
import { ListBuilderService } from '../shared/list-creation/service/list-builder.service';
import { GermplasmListBuilderService } from '../shared/list-creation/service/germplasm-list-builder.service';

@Component({
    selector: 'jhi-germplasm-manager',
    templateUrl: './germplasm-manager.component.html',
    providers: [
        { provide: ListBuilderService, useClass: GermplasmListBuilderService },
    ],
})
export class GermplasmManagerComponent implements OnInit {

    helpLink: string;

    constructor(private paramContext: ParamContext,
                private helpService: HelpService,
                public listBuilderContext: ListBuilderContext
    ) {
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
