import { Component, Input } from '@angular/core';

@Component({
    selector: 'jhi-col-vis-button',
    template: `
        <button (click)="hiddenColumns[colName] = !hiddenColumns[colName]"
                class="btn btn-light"
                [class.active]="!hiddenColumns[colName]">
            <ng-content></ng-content>
        </button>
    `
})
export class ColVisButtonComponent {
    @Input() colName: string;
    @Input() hiddenColumns: any;

}
