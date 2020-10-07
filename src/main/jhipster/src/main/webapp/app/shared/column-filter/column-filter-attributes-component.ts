import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/switchMap';
import { AttributesService } from '../attributes/service/attributes.service';

@Component({
    selector: 'jhi-column-filter-attributes',
    template: `
		<form #f='ngForm'>
			<section class="filter-wrapper">
				<div class="keyword-wrapper">
					<input type="text" class="form-control" [formControl]="queryField" id="keyword" placeholder="search attributes..." autofocus/>
				</div>
				<ul class="filter-select">
					<li *ngFor="let result of results" class="filter-select-list" (click)="addAttribute(result.code)">
						Code: {{result.code}} - Name: {{result.name}}</li>
				</ul>
			</section>
			<div *ngIf="this.filter.attributes.length === 0"><span>Search for attributes that you want to filter</span></div>
			<br/>
			<div *ngFor="let item of filter.attributes">
				<label for="{{item.code}}">{{item.code}}</label>
				<div class="form-group">
					<div class="input-group">
						<input type="text" class="form-control" [(ngModel)]="item.value" name="{{item.code}}">
						<div class="input-group-append">
							<button class="btn btn-danger float-right fa fa-minus" (click)="deleteAttribute(item.code)"></button>
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

    @Output() onApply = new EventEmitter();
    @Output() onReset = new EventEmitter();

    constructor(private attributesService: AttributesService) {
    }

    ngOnInit(): void {
        this.queryField.valueChanges
            .debounceTime(500)
            .distinctUntilChanged()
            .switchMap((query) => this.attributesService.searchAttributes(query))
            .subscribe(result => {
                if (result.status === 400) {
                    return;
                } else {
                    this.results = result.body;
                }
            });
    }

    addAttribute(code) {
        // Reset query field value
        this.queryField.setValue('');
        // Do not add attribute if it's already in the list
        if (!this.filter.attributes.some(e => e.code === code)) {
            this.filter.attributes.push({ code: code, value: '' });
        }
    }

    deleteAttribute(code) {
        this.filter.attributes = this.filter.attributes.filter(e => e.code !== code);
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
