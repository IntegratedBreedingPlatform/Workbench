import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
    selector: 'jhi-column-filter-text-with-match-options',
    template: `
		<form #f='ngForm'>
			<div class="form-check">
				<input class="form-check-input" type="radio" id="radio-0" name="option-group"
					   [value]="MatchType.STARTSWITH" [(ngModel)]="filter.matchType">
				<label class="form-check-label" for="radio-0">
					Starts with
				</label>
			</div>
            <div class="form-check">
                <input class="form-check-input" type="radio" id="radio-1" name="option-group"
                       [value]="MatchType.ENDSWITH" [(ngModel)]="filter.matchType">
                <label class="form-check-label" for="radio-1">
                    Ends with
                </label>
            </div>
			<div class="form-check">
				<input class="form-check-input" type="radio" id="radio-2" name="option-group"
					   [value]="MatchType.EXACTMATCH" [(ngModel)]="filter.matchType">
				<label class="form-check-label" for="radio-2">
					Exact Match
				</label>
			</div>
			<div class="form-check">
				<input class="form-check-input" type="radio" id="radio-3" name="option-group"
					   [value]="MatchType.CONTAINS" [(ngModel)]="filter.matchType">
				<label class="form-check-label" for="radio-3">
					Contains
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
    MatchType = MatchType;
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
    CONTAINS = 'CONTAINS',
    ENDSWITH = 'ENDSWITH'
}
