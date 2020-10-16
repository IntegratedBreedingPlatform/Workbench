import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
    selector: 'jhi-column-filter-date',
    template: `
        <!-- TODO bootstrap validation. :valid pseudo-classes not working -->
        <!--<form #f='ngForm' class="needs-validation" [ngClass]="{'was-validated': f.touched}">-->
        <form #f='ngForm'>
            <div class="form-group row">
                <label for="from" class="col-form-label col-md-3">From</label>
                <div class="input-group col-md-9">
                    <input class="form-control" placeholder="yyyy-mm-dd"
                           name="from" [(ngModel)]="filter.from" [maxDate]="filter.to"
                           ngbDatepicker [autoClose]="false" #from="ngbDatepicker" id="from">
                    <div class="input-group-append">
                        <button class="btn btn-outline-secondary calendar" (click)="from.toggle()" type="button"></button>
                    </div>
                </div>
            </div>
            <div class="form-group row">
                <label for="to" class="col-form-label col-md-3">To</label>
                <div class="input-group col-md-9">
                    <input class="form-control" placeholder="yyyy-mm-dd"
                           name="to" [(ngModel)]="filter.to" [minDate]="filter.from"
                           ngbDatepicker [autoClose]="false" #to="ngbDatepicker" id="to">
                    <div class="input-group-append">
                        <button class="btn btn-outline-secondary calendar" (click)="to.toggle()" type="button"></button>
                    </div>
                </div>
            </div>
            <div class="footer text-center"><br>
                <button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)">Apply</button>
                <button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
            </div>
        </form>
    `
})
export class ColumnFilterDateComponent implements OnInit {
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
