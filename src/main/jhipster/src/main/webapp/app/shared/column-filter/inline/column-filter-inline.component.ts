import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { FilterType } from '../column-filter.component';
import { JhiEventManager } from 'ng-jhipster';

/**
 * This inline filter is intended to be more simple than the badge-like (../column-filter.component)
 * Some action are delegated entirely to the caller (e.g deciding what to do with the filter)
 */
@Component({
    selector: 'jhi-column-filter-inline',
    templateUrl: './column-filter-inline.component.html',
    styleUrls: ['./../column-filter.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ColumnFilterInlineComponent {
    FILTER_TYPES = FilterType;

    @Input() filter: any;

    @Output() onApply = new EventEmitter<any>();
    @Output() onReset = new EventEmitter<any>();

    constructor(
        private eventManager: JhiEventManager
    ) {
    }

    apply() {
        this.onApply.emit();
    }

    reset() {
        this.onReset.emit();
    }
}
