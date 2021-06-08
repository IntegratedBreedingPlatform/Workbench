import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { VariableDetails } from '../ontology/model/variable-details';
import { VariableService } from '../ontology/service/variable.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../alert/format-error-list';
import { AlertService } from '../alert/alert.service';
import { finalize } from 'rxjs/operators';
import { Select2OptionData } from 'ng-select2';

declare const $: any;

@Component({
    selector: 'jhi-variable-select',
    templateUrl: './variable-select.component.html'
})
export class VariableSelectComponent implements OnInit {
    @Input() name: string;
    @Input() id: string;

    @Input() value: any;
    initialData: Select2OptionData[];

    @Input() disabled: boolean;
    @Input() multiple: boolean;
    @Input() variableTypeIds: number[];
    @Output() onVariableSelectedChange: EventEmitter<{ [key: string]: VariableDetails }> = new EventEmitter<{ [key: string]: VariableDetails }>()

    options = {
        templateResult(item: any) {
            // Performance warning: Do not add logic inside this method other than the template
            return $(`
                <div>
                    <img class="variable-select-icon" alt="Property" src="/ibpworkbench/controller/static/images/property.svg">
                    <span>${item.propertyName}</span>
                    <span>${item.classes}</span>
                </div>
                <div>
                    <img class="variable-select-icon" alt="Variable" src="/ibpworkbench/controller/static/images/variable.png">
                    <span>${item.displayName}</span>
                </div>
            `);
        },
        /*
         * FIXME ng-select2 still relies on legacy matcher format
         *  https://github.com/tealpartners/ng-select2/blob/b0fdd0f1329f4cca3267f8507c7d32dc29f0f281/projects/ng-select2/src/lib/ng-select2.component.ts#L197
         *  we are using select2.full (with compat/matcher) for now
         */
        matcher(term: string, text: string, item: VariableDetails) {
            const termLowerCase = term.toLocaleLowerCase();
            return (item.alias && item.alias.toLocaleLowerCase().includes(termLowerCase))
                || item.name.toLocaleLowerCase().includes(termLowerCase);
        },
        multiple: false
    }
    variables: any[];

    variableById: { [key: string]: VariableDetails } = {};
    isLoading = true;

    constructor(
        private variableService: VariableService,
        private alertService: AlertService
    ) {
        this.variableService.getVariables().pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((variables) => {
            this.variables = this.transform(variables)
            if (this.value) {
                const variable = this.variableById[this.value];
                this.initialData = [{ id: variable.id, text: variable.alias || variable.name }]
            }
        }, (error) => {
            this.onError(error);
        });
    }

    ngOnInit(): void {
        this.options.multiple = this.multiple;
    }

    transform(variables: VariableDetails[]): any[] {
        return variables.filter((variable) => {
            if (this.variableTypeIds && this.variableTypeIds.length) {
                return variable.variableTypes
                    && variable.variableTypes.length
                    && variable.variableTypes.some(
                        (variableType) => this.variableTypeIds.includes(Number(variableType.id))
                    );
            }
            return true;
        }).map((variable) => {
            this.variableById[variable.id] = variable;

            const copy: any = Object.assign({}, variable);
            const displayName = copy.alias
                ? copy.alias + ' (' + copy.name + ')'
                : copy.name;
            return Object.assign(copy, {
                id: copy.id,
                text: copy.alias || copy.name,
                displayName,
                propertyName: copy.property ? copy.property.name : '',
                classes: copy.property ? '(' + copy.property.classes.join(', ') + ')' : '',
                alias: copy.alias ? copy.alias + '(' + copy.name + ')' : ''
            });
        });
    }

    onNgModelChange() {
        if (!this.value) {
            return;
        }
        const event = this.multiple
            ? this.value.reduce((prev, id) => (prev[id] = this.variableById[id], prev), {})
            : this.variableById[this.value];
        this.onVariableSelectedChange.emit(event);
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }
}
