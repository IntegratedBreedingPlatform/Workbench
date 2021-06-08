import { Component } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { ActivatedRoute } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-var-test',
    template: `
		<h2>Variable select prototype</h2>
		<div class="form-group row">
			<div class="col-sm-3">
				<jhi-variable-select [multiple]="multiple"
									 (onVariableSelectedChange)="log($event)"
									 [variableTypeIds]="variableTypeIds"></jhi-variable-select>
			</div>
		</div>
        <jhi-alert></jhi-alert>
    `
})
export class VariableSelectTestComponent {
    multiple = false;
    variableTypeIds = [];

    constructor(
        private paramContext: ParamContext,
        private route: ActivatedRoute,
        private jhiLanguageService: JhiLanguageService
    ) {
        this.paramContext.readParams();
        const queryParamMap = this.route.snapshot.queryParamMap;
        this.multiple = Boolean(queryParamMap.get('multiple'));
        const variableTypeIdsParam = queryParamMap.get('variableTypeIds');
        this.variableTypeIds = variableTypeIdsParam && variableTypeIdsParam.split(',') || [];
    }

    log(event) {
        console.log(event)
    }
}
