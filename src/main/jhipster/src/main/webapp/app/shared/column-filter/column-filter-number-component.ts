import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { isNumeric } from '../util/is-numeric';

@Component({
    selector: 'jhi-column-filter-number',
    template: `
        <form #f='ngForm' [formGroup]="form">
            <div class="form-group row">
                <label for="min" class="col-form-label col-md-3">Min</label>
                <div class="col-md-9">
                    <input type="number" class="form-control" id="min" placeholder="min value" formControlName="min">
                </div>
            </div>
            <div class="form-group row">
                <label for="max" class="col-form-label col-md-3">Max</label>
                <div class="col-md-9">
                    <input type="number" class="form-control" id="max" placeholder="max value" formControlName="max">
                </div>
            </div>
            <div class="alert alert-danger" *ngIf="form.hasError('minMaxError')">
                Max must be higher than min
            </div>
            <div class="footer text-center"><br>
                <button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply()">Apply</button>
                <button class="btn btn btn-default btn-sm" (click)="reset()">Reset</button>
            </div>
        </form>
    `
})
export class ColumnFilterNumberComponent implements OnInit {
    @Input() filter: any;

    form: FormGroup;

    @Output() onApply = new EventEmitter();
    @Output() onReset = new EventEmitter();

    ngOnInit(): void {
        // Made with reactive forms in case min validator is needed: [Validators.min(0)]
        // github: angular/angular/issues/30409
        this.form = new FormGroup({
            'min': new FormControl(this.filter.min),
            'max': new FormControl(this.filter.max)
        }, { validators: minMaxValidator });
    }

    apply() {
        if (!this.form.valid) {
            return;
        }
        this.filter.min = this.form.get('min').value;
        this.filter.max = this.form.get('max').value;
        this.onApply.emit();
    }

    reset() {
        this.form.reset();
        this.onReset.emit();
    }
}

export const minMaxValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
    const min = control.get('min');
    const max = control.get('max');

    return isNumeric(min.value) && isNumeric(max.value)
    && min.value > max.value ? { 'minMaxError': true } : null;
};
