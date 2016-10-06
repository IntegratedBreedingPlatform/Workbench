import {
    Component, OnInit, OnChanges, SimpleChange, Input, Output, EventEmitter
} from '@angular/core';
import { Observable } from 'rxjs/Rx';

@Component({
    selector: 'pagination',
    moduleId: module.id,
    templateUrl: 'pagination.component.html'
})
export class PaginationComponent implements OnInit, OnChanges {
    @Input() maxPageIndex: number;
    @Input() rowChanged: Observable<number>;
    @Output() pageNumberChanged = new EventEmitter();
    currentPageNumber: number = 1;

    ngOnInit() {
        this.setCurrentPage(1);
     }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }) {
        if (changes['maxPageIndex']) {
            let change = changes['maxPageIndex'];
            if (this.currentPageNumber > change.currentValue) {
                // throws ExpressionChangedAfterItHasBeenCheckedException
                // if there's no setTimeout.
                // no need to add setTimeout if ngOnChanges
                // is fired after changes made on root component.
                setTimeout(() => this.setCurrentPage(1), 1);
            }
        }
    }

    setCurrentPage(pageNumber: number, event?: MouseEvent): void {
        if (event) {
            event.preventDefault();
        }
        if (pageNumber === 0 || pageNumber > this.maxPageIndex
            || pageNumber === this.currentPageNumber) {
            return;
        }

        this.pageNumberChanged.emit(pageNumber);

        if (!this.rowChanged) {
            this.currentPageNumber = pageNumber;
        }
    }

    range(min: number, max: number): number[] {
        let result = [];
        for (let i = min; i <= max; i++) {
            result.push(i);
        }
        return result;
    }

    get pageStartNumber(): number {
        let startNumber = this.currentPageNumber <= 4
            ? 1
            : this.currentPageNumber >= this.maxPageIndex - 3
                ? this.maxPageIndex - 6
                : this.currentPageNumber - 3;
        return startNumber < 1 ? 1 : startNumber;
    }

    get pageEndNumber(): number {
        let pageEnd = this.pageStartNumber + 6;
        return pageEnd > this.maxPageIndex ? this.maxPageIndex : pageEnd;
    }
}
