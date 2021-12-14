import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
    selector: 'jhi-column-filter-list',
    template: `
        <form #f='ngForm' class="needs-validation" [ngClass]="{'was-validated': f.touched}">
            <input type="text" class="form-control" placeholder="comma-separated values"
                   pattern="^[\\d]+(,[\\d]+)*$" [(ngModel)]="filter.value" name="{{filter.key}}" data-test="columnFilterListInput">
            <br/>
            <div class="footer text-center"><br>
                <button type="submit" class="btn btn-primary btn-sm" [disabled]="!f.valid" (click)="apply(f)" data-test="columnFilterListApplyButton">Apply</button>
                <button class="btn btn btn-default btn-sm" (click)="reset(f)">Reset</button>
            </div>
        </form>
    `
})
export class ColumnFilterListComponent implements OnInit {
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
