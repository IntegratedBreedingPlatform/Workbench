import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { SampleComponent, SampleBrowseComponent } from './';
import { SampleDetailComponent } from './sample-detail.component';
import { SamplePopupComponent } from './sample-dialog.component';
import { SampleDeletePopupComponent } from './sample-delete-dialog.component';

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
        path: 'sample-browse',
        component: SampleBrowseComponent,
        resolve: {
            'pagingParams': SampleResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        },
    }, {
        path: 'sample',
        component: SampleComponent,
        resolve: {
            'pagingParams': SampleResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        }
    }, {
        path: 'sample/:id',
        component: SampleDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        }
    }
];

export const samplePopupRoute: Routes = [
    {
        path: 'sample-new',
        component: SamplePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        },
        outlet: 'popup'
    },
    {
        path: 'sample/:id/edit',
        component: SamplePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        },
        outlet: 'popup'
    },
    {
        path: 'sample/:id/delete',
        component: SampleDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sample.home.title'
        },
        outlet: 'popup'
    }
];
