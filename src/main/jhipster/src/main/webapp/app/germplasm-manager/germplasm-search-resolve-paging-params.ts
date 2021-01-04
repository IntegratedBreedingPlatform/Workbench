import { Resolve } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import { RouterStateSnapshot } from '@angular/router';

/**
 * Key used indicate NO sorting at all.
 * Sorting turns the germplasm search into search mode (limited to 5000)
 */
export const SORT_PREDICATE_NONE = 'SORT_PREDICATE_NONE';

@Injectable()
export class GermplasmSearchResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : SORT_PREDICATE_NONE;
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
        };
    }
}
