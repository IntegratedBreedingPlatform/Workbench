import { Component } from '@angular/core';
import { Input } from '@angular/core';

/**
 * A copy of jhipster item-count component with additional params
 */
@Component({
    selector: 'jhi-item-count-custom',
    template: `
		<span class="info jhi-item-count">
            Showing {{((page - 1) * itemsPerPage) == 0 ? 1 : ((page - 1) * itemsPerPage + 1)}} -
			{{(page * itemsPerPage) < total ? (page * itemsPerPage) : total}}
			of <span data-test="totalCount" class="font-weight-bold">{{total}}{{+total < limit ? '' : '+'}}</span>
            items.
        </span>`
})
export class ItemCountCustomComponent {

    @Input() page: number;
    @Input() total: number;
    @Input() limit: number;
    @Input() itemsPerPage: number;

    constructor() {
    }

}
