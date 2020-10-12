import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
    selector: 'jhi-column-filter-boolean',
    template: `
		<form #f='ngForm'>
			<div class="form-check">
				<input type="checkbox" class="form-check-input" [(ngModel)]="filter.value" name="{{filter.key}}">
				<label class="form-check-label" for="{{filter.key}}">
					{{filter.name}}
				</label>
			</div>
			<br/>
			<div class="footer text-center"><br>
				<button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)">Apply</button>
				<button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
			</div>
		</form>
    `
})
export class ColumnFilterBooleanComponent implements OnInit {
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
        this.filter.value = true;
        this.onReset.emit();
    }
}
