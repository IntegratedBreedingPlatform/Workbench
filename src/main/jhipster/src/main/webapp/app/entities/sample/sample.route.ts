import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { SampleComponent, SampleManagerComponent } from './';

@Injectable()
export class SampleResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const sampleRoute: Routes = [
    {
        path: 'sample-manager',
        component: SampleManagerComponent,
        resolve: {
            'pagingParams': SampleResolvePagingParams
        },
        data: {
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        },
    }, {
        path: 'sample',
        component: SampleComponent,
        resolve: {
            'pagingParams': SampleResolvePagingParams
        },
        data: {
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        }
    }
];

// TODO Removing jhipster samplePopup component
//  but leaving this as a reminder to try again ng-bootstrap (when animations are available)
//  and popup outlet
/*
export const samplePopupRoute: Routes = [
    {
        path: 'sample-new',
        component: SamplePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        },
        outlet: 'popup'
    }
];
*/
