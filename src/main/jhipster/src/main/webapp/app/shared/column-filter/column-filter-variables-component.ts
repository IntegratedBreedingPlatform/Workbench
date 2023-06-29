import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl} from '@angular/forms';
import {VariableService} from '../ontology/service/variable.service';
import {VariableSearchRequest} from '../ontology/model/variable-search-request.model';
import {MatchType} from './column-filter-text-with-match-options-component';
import {Variable} from '../ontology/model/variable';
import {HttpResponse} from '@angular/common/http';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import {EMPTY} from 'rxjs';

@Component({
    selector: 'jhi-column-filter-variables',
    template: `
        <form #f='ngForm'>
            <section class="filter-wrapper">
                <div class="keyword-wrapper">
                    <input type="text" class="form-control" [formControl]="queryField" id="keyword"
                           placeholder="{{filter.placeholder || 'Search variables...'}}" autofocus/>
                </div>
                <ul class="filter-select">
                    <li *ngFor="let result of results" class="filter-select-list" (click)="addVariable(result)">
                        <div>
                            <img class="variable-select-icon" alt="Property"
                                 src="/ibpworkbench/controller/static/images/property.svg">
                            <span> {{result.property.name}} </span>
                        </div>
                        <div>
                            <img class="variable-select-icon" alt="Variable"
                                 src="/ibpworkbench/controller/static/images/variable.png">
                            <span *ngIf="result.alias" class="label-info"> {{result.alias}} ({{result.name}})</span>
                            <span *ngIf="!result.alias" class="label-info"> {{result.name}}</span>
                        </div>
                    </li>
                </ul>
            </section>
            <div *ngIf="this.filter.variables.length === 0">
                <span>{{filter.description || 'Search for variables that you want to filter'}}</span></div>
            <br/>
            <div *ngFor="let variable of filter.variables">
                <div class="form-group">
                    <label *ngIf="variable.alias" for="{{variable.name}}">{{variable.name}} ({{variable.alias}})</label>
                    <label *ngIf="!variable.alias" for="{{variable.name}}">{{variable.name}}</label>
                    <div class="input-group">
                        <input type="text" class="form-control" [(ngModel)]="variable.value" name="{{variable.name}}">
                        <div class="input-group-append">
                            <button class="btn btn-default float-right fa fa-minus"
                                    (click)="deleteVariable(variable)"></button>
                        </div>
                    </div>
                </div>
            </div>
            <br/>
            <div class="footer text-center"><br>
                <button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)">Apply
                </button>
                <button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
            </div>
        </form>
    `
})
export class ColumnFilterVariablesComponent implements OnInit {

    queryField: FormControl = new FormControl();
    results: Variable[] = [];

    @Input() filter: any;

    @Output() onAdd = new EventEmitter();
    @Output() onDelete = new EventEmitter();
    @Output() onApply = new EventEmitter();
    @Output() onReset = new EventEmitter();

    constructor(private variableService: VariableService) {
    }

    ngOnInit(): void {
        this.queryField.valueChanges.pipe(
            debounceTime(500),
            distinctUntilChanged(),
            switchMap((query) => {
                if (!query) {
                    this.results = [];
                    return EMPTY;
                }
                const request: VariableSearchRequest = <VariableSearchRequest>({});
                request.nameFilter = {
                    value: query,
                    type: MatchType.CONTAINS
                };
                if (this.filter.variableTypeIds) {
                    request.variableTypeIds = this.filter.variableTypeIds;
                }
                return this.variableService.searchVariables(request);
            })
        )
            .subscribe((result: HttpResponse<Variable[]>) => {
                this.results = result.body;
            });
    }

    addVariable(variable: Variable) {
        // Reset query field value
        this.queryField.setValue('');
        // Do not add variable if it's already in the list
        if (!this.filter.variables.some((e) => e.id === variable.id)) {
            this.filter.variables.push({...variable, value: ''});
        }
        this.onAdd.emit(variable);
    }

    deleteVariable(variable: Variable) {
        this.filter.variables = this.filter.variables.filter((e) => e.id !== variable.id);
        this.onDelete.emit(variable);
    }

    apply(form) {
        if (!form.valid) {
            return;
        }
        this.onApply.emit();
    }

    reset(form) {
        form.reset();
        this.onReset.emit();
    }
}
