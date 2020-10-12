import { Component, Input } from '@angular/core';
import { Output } from '@angular/core';
import { EventEmitter } from '@angular/core';

@Component({
    selector: 'jhi-col-vis-button',
    styles: [`.text-wrap {
        white-space:normal !important;
        word-wrap: break-word;
        word-break: normal;
    }`],
    template: `
        <button (click)="toggle()"
                class="btn btn-light text-wrap"
                [class.active]="!hiddenColumns[colName]">
            <ng-content></ng-content>
        </button>
    `
})
export class ColVisButtonComponent {
    @Input() colName: string;
    @Input() hiddenColumns: any;

    @Output() onToggle: EventEmitter<any> = new EventEmitter();

    toggle() {
        this.hiddenColumns[this.colName] = !this.hiddenColumns[this.colName];
        if (this.onToggle) {
            this.onToggle.emit();
        }
    }

}
