import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/switchMap';
import { Select2OptionData } from 'ng-select2/lib/ng-select2.interface';

@Component({
    selector: 'jhi-column-filter-dropdown',
    template: `
		<form #f='ngForm'>
			<section class="filter-wrapper">
				<div id="dropdown-container-{{this.filter.key}}">
					<ng-select2 width="170px"
								[name]="'dropdownFilter' + this.filter.key"
								[id]="'dropdownFilter' + this.filter.key"
								[placeholder]="'Select option' + (this.multipleSelect ? 's' : '')"
								[allowClear]="true"
								[(ngModel)]="selectedValues"
								[data]="this.values"
								[options]="this.options"
								[dropdownParent]="'dropdown-container-' + this.filter.key"
								(ngModelChange)="onNgModelChange()">
					</ng-select2>
				</div>
			</section>
			<div *ngIf="this.multipleSelect"><span>Please select one or more values</span></div>
			<div *ngIf="!this.multipleSelect"><span>Please select one value</span></div>
			<br/>
			<div class="footer text-center"><br>
				<button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)">Apply</button>
				<button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
			</div>
		</form>
    `
})
export class ColumnFilterDropdownComponent implements OnInit {

    values: Select2OptionData[];
    selectedValues: string[];
    multipleSelect: boolean;

    @Input() filter: any;

    @Output() onApply = new EventEmitter();
    @Output() onReset = new EventEmitter();

    options;

    constructor() {
    }

    async ngOnInit() {
       this.values = await this.filter.values;
       this.multipleSelect = (this.filter.multipleSelect) ? this.filter.multipleSelect : false;
       this.options = {
           multiple: this.multipleSelect
       }
    }

    apply(form) {
        if (!form.valid) {
            return;
        }
        this.filter['selectedOptions'] = this.values.filter((value) => this.selectedValues.indexOf(value.id) > -1);
        this.onApply.emit();
    }

    reset(form) {
        form.reset();
        this.onReset.emit();
    }

    // isOptionSelected(option: ColumnFilterDropdownOption) {
    //     return this.selectedOptions &&
    //         this.selectedOptions.filter((selectedOption: ColumnFilterDropdownOption) => option.id === selectedOption.id).length === 1;
    // }

    onNgModelChange() {

    }

    clickedOption() {
        console.log(this.selectedValues);
    }
}
