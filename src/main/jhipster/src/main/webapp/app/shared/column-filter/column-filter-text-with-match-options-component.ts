import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
    selector: 'jhi-column-filter-text-with-match-options',
    template: `
		<form #f='ngForm'>
			<div class="form-check" *ngFor="let option of filter.options | async">
				<input class="form-check-input" type="radio" id="radio-{{option.id}}" name="option-group-{{filter.key}}"
					   [value]="option.id" [(ngModel)]="filter.matchType">
				<label class="form-check-label" for="radio-{{option.id}}">
					{{option.name}}
				</label>
			</div>
			<br/>
			<input type="text" class="form-control" placeholder="{{filter.placeholder || 'Contains Text'}}" [(ngModel)]="filter.value" name="{{filter.key}}">
			<br/>
			<div class="footer text-center"><br>
				<button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)">Apply</button>
				<button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
			</div>
		</form>
    `
})
export class ColumnFilterTextWithMatchOptionsComponent implements OnInit {
    @Input() filter: any;

    @Output() onApply = new EventEmitter();
    @Output() onReset = new EventEmitter();

    ngOnInit(): void {
    }

    apply(form) {
        if (!form.valid) {
            return;
        }
        this.onApply.emit();
    }

    reset(form) {
        this.filter.value = '';
        this.onReset.emit();
    }
}

export enum MatchType {
    STARTSWITH = 'STARTSWITH',
    EXACTMATCH = 'EXACTMATCH',
    CONTAINS = 'CONTAINS'
}
