import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_GERMPLASM_LIST } from '../app.constants';
import { JhiLanguageService } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { GermplasmTreeTableComponent } from '../shared/tree/germplasm/germplasm-tree-table.component';

@Component({
    selector: 'jhi-germplasm-list',
    templateUrl: './germplasm-list.component.html'
})
export class GermplasmListComponent implements OnInit {

    helpLink: string;

    constructor(private paramContext: ParamContext,
                private helpService: HelpService,
                private jhiLanguageService: JhiLanguageService
    ) {
        this.paramContext.readParams();
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_GERMPLASM_LIST).toPromise().then((response) => {
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

@Component({
    selector: 'jhi-germplasm-list-browse-popup',
    template: ``
})
export class GermplasmListBrowsePopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        this.popupService.open(GermplasmTreeTableComponent as Component, { size: 'lg', backdrop: 'static' });
    }

}
