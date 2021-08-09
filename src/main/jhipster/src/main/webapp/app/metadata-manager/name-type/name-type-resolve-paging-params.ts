import { JhiPaginationUtil } from 'ng-jhipster';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Resolve } from '@angular/router';
import { SORT_PREDICATE_NONE } from '../../germplasm-manager/germplasm-search-resolve-paging-params';
import { Injectable } from '@angular/core';

@Injectable()
export class NameTypeResolvePagingParams implements Resolve<any> {
    constructor(private paginationUtil: JhiPaginationUtil) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'fcode,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
        };
    }
}
