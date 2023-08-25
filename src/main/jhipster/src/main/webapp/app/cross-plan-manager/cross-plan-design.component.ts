import {Component, OnInit} from '@angular/core';
import {ParamContext} from '../shared/service/param.context';
import {HelpService} from '../shared/service/help.service';
import {HELP_CROSS_PLAN_DESIGN} from '../app.constants';
import {TranslateService} from '@ngx-translate/core';
import {ListEntry} from '../shared/list-builder/model/list.model';
import {CrossPlanDesignService} from '../shared/cross-plan-design/service/cross-plan-design.service';
import {CrossingMethod, CrossPlanDesignInput} from '../shared/cross-plan-design/model/cross-plan-design-input';
import {ColumnLabels} from './preview-crosses.component';
@Component({
    selector: 'jhi-cross-plan-design',
    templateUrl: './cross-plan-design.component.html'
})
export class CrossPlanDesignComponent implements OnInit {

    helpLink: string;

    femaleListData: number[];
    maleListData: number[];
    previewCrosses: ListEntry[];

    maleParents = 'cross-plan.design.male.parent.list';
    maleParentsTable = 'maleParentsTable';
    femaleParents = 'cross-plan.design.female.parent.list';
    femaleParentsTable = 'femaleParentsTable';

    disableCheckboxMakeReciprocal = false;
    disableCheckboxExcludeSelfs = false;

    makeReciprocal = false;
    excludeSelfs = false;
    crossesMethodSelected: CrossingMethod = CrossingMethod.PLEASE_CHOOSE;

    isSaving = false;
    isLoading = false;

    crossPlanDesignInput: CrossPlanDesignInput;
    CrossingMethod = CrossingMethod;

    constructor(
        private helpService: HelpService,
        private translateService: TranslateService,
        private paramContext: ParamContext,
        public crossPlanDesignService: CrossPlanDesignService) {

        this.paramContext.readParams();
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_CROSS_PLAN_DESIGN).toPromise().then((response) => {
                if (response.body) {
                    this.helpLink = response.body;
                }
            }).catch((error) => {
            });
        }

        this.femaleListData = [];
        this.maleListData = [];
        this.previewCrosses = [];
    }

    ngOnInit(): void {
    }

    onMethodChange() {
        if (this.crossesMethodSelected === CrossingMethod.CROSS_EACH_FEMALE_WITH_AN_UNKNOWN_MALE_PARENT) {
            this.excludeSelfs = true;
            this.makeReciprocal = false;
            this.disableCheckboxMakeReciprocal = true;
            this.disableCheckboxExcludeSelfs = true;
        } else if (this.crossesMethodSelected === CrossingMethod.CROSS_EACH_FEMALE_WITH_ALL_MALE_PARENTS) {
            this.makeReciprocal = false;
            this.disableCheckboxMakeReciprocal = true;
        } else {
            this.disableCheckboxMakeReciprocal = false;
            this.disableCheckboxExcludeSelfs = false;
        }

    }

    generateCrosses() {
        this.crossPlanDesignInput = new CrossPlanDesignInput(this.femaleListData, this.maleListData, this.makeReciprocal, this.excludeSelfs, this.crossesMethodSelected);
        console.log(this.crossPlanDesignInput);
        this.crossPlanDesignService.generateCrossPreview(this.crossPlanDesignInput).toPromise().then((response) => {
            console.log(response)
            this.previewCrosses = this.buildListEntry(response);
        }).catch((error) => {
        });
    }

    buildListEntry(data): ListEntry[] {
        return data.map((crosspreview: any) => {
            const row: ListEntry = new ListEntry();
            row[ColumnLabels.FEMALE_PARENT] = crosspreview.femaleParent;
            row[ColumnLabels.MALE_PARENT] = crosspreview.maleParent;
            row[ColumnLabels.FEMALE_CROSS] = crosspreview.femaleCross;
            row[ColumnLabels.MALE_CROSS] = crosspreview.maleCross;
            row[ColumnLabels.GERMPLASM_ORIGIN] = crosspreview.germplasmOrigin;
            return row;
        });
    }

    back() {

    }

    save() {

    }

    isSaveDisable() {

    }

}