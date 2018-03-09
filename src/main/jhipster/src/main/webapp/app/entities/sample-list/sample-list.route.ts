import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { SampleListComponent } from './sample-list.component';
import { SampleListDetailComponent } from './sample-list-detail.component';
import { SampleListPopupComponent } from './sample-list-dialog.component';
import { SampleListDeletePopupComponent } from './sample-list-delete-dialog.component';

@Injectable()
export class SampleListResolvePagingParams implements Resolve<any> {

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

export const sampleListRoute: Routes = [
    {
        path: 'sample-list',
        component: SampleListComponent,
        resolve: {
            'pagingParams': SampleListResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sampleList.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'sample-list/:id',
        component: SampleListDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sampleList.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const sampleListPopupRoute: Routes = [
    {
        path: 'sample-list-new',
        component: SampleListPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sampleList.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'sample-list/:id/edit',
        component: SampleListPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sampleList.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'sample-list/:id/delete',
        component: SampleListDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'bmsjHipsterApp.sampleList.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
