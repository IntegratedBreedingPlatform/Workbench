import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/switchMap';
import { GermplasmService } from '../germplasm/service/germplasm.service';

@Component({
    selector: 'jhi-column-filter-attributes',
    template: `
		<form #f='ngForm'>
			<section class="filter-wrapper">
				<div class="keyword-wrapper">
					<input type="text" class="form-control" [formControl]="queryField" id="keyword" placeholder="search attributes..." autofocus/>
				</div>
				<ul class="filter-select">
					<li *ngFor="let result of results" class="filter-select-list" (click)="addAttribute(result)">
						<label class="label-info">Name:</label> {{result.name}} - <label class="label-info">Definition:</label> {{result.definition}}
					</li>
				</ul>
			</section>
			<div *ngIf="this.filter.attributes.length === 0"><span>Search for attributes that you want to filter</span></div>
			<br/>
			<div *ngFor="let attribute of filter.attributes">
				<div class="form-group">
					<label *ngIf="attribute.alias" for="{{attribute.name}}">{{attribute.name}} ({{attribute.alias}})</label>
					<label *ngIf="!attribute.alias" for="{{attribute.name}}">{{attribute.name}}</label>
					<div class="input-group">
						<input type="text" class="form-control" [(ngModel)]="attribute.value" name="{{attribute.name}}">
						<div class="input-group-append">
							<button class="btn btn-default float-right fa fa-minus" (click)="deleteAttribute(attribute)"></button>
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
export class ColumnFilterAttributesComponent implements OnInit {

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
            .debounceTime(500)
            .distinctUntilChanged()
            .switchMap((query) => this.germplasmService.searchAttributes(query))
            .subscribe((result) => {
                if (result.status === 400) {
                    return;
                } else {
                    this.results = result.body;
                }
            });
    }

    addAttribute(attribute) {
        // Reset query field value
        this.queryField.setValue('');
        // Do not add attribute if it's already in the list
        if (!this.filter.attributes.some((e) => e.name === attribute.name)) {
            this.filter.attributes.push({ ...attribute, value: '' });
        }
        this.onAdd.emit(attribute);
    }

    deleteAttribute(attribute) {
        this.filter.attributes = this.filter.attributes.filter((e) => e.name !== attribute.name);
        this.onDelete.emit(attribute);
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
