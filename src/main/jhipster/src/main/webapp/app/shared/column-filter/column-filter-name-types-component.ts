import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { GermplasmService } from '../germplasm/service/germplasm.service';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';

@Component({
    selector: 'jhi-column-filter-name-types',
    template: `
		<form #f='ngForm'>
			<section class="filter-wrapper">
				<div class="keyword-wrapper">
					<input type="text" class="form-control" [formControl]="queryField" id="keyword" placeholder="search name types..." autofocus/>
				</div>
				<ul class="filter-select">
					<li *ngFor="let result of results" class="filter-select-list" (click)="addNameType(result)">
						<label class="label-info">Code:</label> {{result.code}} - <label class="label-info">Name:</label> {{result.name}}</li>
				</ul>
			</section>
			<div *ngIf="this.filter.nameTypes.length === 0"><span>Search for name types that you want to filter</span></div>
			<br/>
			<div *ngFor="let nameType of filter.nameTypes">
				<div class="form-group">
					<label for="{{nameType.code}}">{{nameType.name}}</label>
					<div class="input-group">
						<input type="text" class="form-control" [(ngModel)]="nameType.value" name="{{nameType.code}}">
						<div class="input-group-append">
							<button class="btn btn-default float-right fa fa-minus" (click)="deleteNameType(nameType)"></button>
						</div>
					</div>
				</div>
			</div>
			<br/>
			<div class="footer text-center"><br>
				<button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)">Apply</button>
				<button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
			</div>
		</form>
    `
})
export class ColumnFilterNameTypesComponent implements OnInit {

    queryField: FormControl = new FormControl();
    results: any[] = [];

    @Input() filter: any;

    @Output() onAdd = new EventEmitter();
    @Output() onDelete = new EventEmitter();
    @Output() onApply = new EventEmitter();
    @Output() onReset = new EventEmitter();

    constructor(private germplasmService: GermplasmService) {
    }

    ngOnInit(): void {
        this.queryField.valueChanges
            .pipe(
                debounceTime(500),
                distinctUntilChanged(),
                switchMap((query) => this.germplasmService.searchNameTypes(query))
            )
            .subscribe((result) => {
                if (result.status === 400) {
                    return;
                } else {
                    this.results = result.body;
                }
            });
    }

    addNameType(nameType) {
        // Reset query field value
        this.queryField.setValue('');
        // Do not add name type if it's already in the list
        if (!this.filter.nameTypes.some((e) => e.code === nameType.code)) {
            this.filter.nameTypes.push({ ...nameType, value: '' });
        }
        this.onAdd.emit(nameType);
    }

    deleteNameType(nameType) {
        this.filter.nameTypes = this.filter.nameTypes.filter((e) => e.code !== nameType.code);
        this.onDelete.emit(nameType);
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
