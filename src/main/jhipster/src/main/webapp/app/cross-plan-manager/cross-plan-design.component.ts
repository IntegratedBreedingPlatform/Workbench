import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {ParamContext} from '../shared/service/param.context';
import {HelpService} from '../shared/service/help.service';
import {HELP_CROSS_PLAN_DESIGN} from '../app.constants';
import {TranslateService} from '@ngx-translate/core';
import {GermplasmDetailsUrlService} from '../shared/germplasm/service/germplasm-details.url.service';
import {ListEntry} from '../shared/list-builder/model/list.model';
import {ListParentsContext} from './list-parents-context';

@Component({
    selector: 'jhi-cross-plan-design',
    templateUrl: './cross-plan-design.component.html'
})
export class CrossPlanDesignComponent implements OnInit {

    helpLink: string;

    femaleListData: ListEntry[] = [];
    maleListData: ListEntry[] = [];
    previewCrosses: ListEntry[] = [];

    maleParents = 'cross-plan.design.male.parent.list';
    maleParentsTable = 'maleParentsTable';
    femaleParents = 'cross-plan.design.female.parent.list';
    femaleParentsTable = 'femaleParentsTable';

    makeReciprocal = false;
    excludeSelfs = false;
    crossesMethodSelected: string;
    isSaving = false;
    isLoading = false;

    constructor(
        private helpService: HelpService,
        private translateService: TranslateService,
        private paramContext: ParamContext,
        private context: ListParentsContext,
        public germplasmDetailsUrlService: GermplasmDetailsUrlService,
        public viewContainerRef: ViewContainerRef) {

        this.paramContext.readParams();
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_CROSS_PLAN_DESIGN).toPromise().then((response) => {
                if (response.body) {
                    this.helpLink = response.body;
                }
            }).catch((error) => {
            });
        }
    }

    ngOnInit(): void {
    }

    generateCrosses() {

    }

    back() {

    }

    save() {

    }

    isSaveDisable() {

    }

}