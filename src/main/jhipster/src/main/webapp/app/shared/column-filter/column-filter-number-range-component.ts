import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
    selector: 'jhi-column-filter-number-range',
    template: `
        <!-- TODO bootstrap validation. :valid pseudo-classes not working -->
        <!--<form #f='ngForm' class="needs-validation" [ngClass]="{'was-validated': f.touched}">-->
        <form #f='ngForm'>
            <div class="form-group row">
                <label for="from" class="col-form-label col-md-3">From</label>
                <div class="input-group col-md-9">
                    <input type="number" class="form-control" name="from" [(ngModel)]="filter.from" id="from" placeholder="Start"/>
                </div>
            </div>
            <div class="form-group row">
                <label for="to" class="col-form-label col-md-3">To</label>
                <div class="input-group col-md-9">
                    <input type="number" class="form-control" name="to" [(ngModel)]="filter.to" id="to" placeholder="End"/>
                </div>
            </div>
            <div class="footer text-center"><br>
                <button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)">Apply</button>
                <button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
            </div>
        </form>
    `
})
export class ColumnFilterNumberRangeComponent implements OnInit {
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
        form.reset();
        this.onReset.emit();
    }
}
