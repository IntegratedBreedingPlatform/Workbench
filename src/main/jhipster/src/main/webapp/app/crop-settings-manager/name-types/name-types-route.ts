import { Routes } from '@angular/router';
import { NameTypeEditPopupComponent } from './name-type-edit-dialog.component';
import { NameTypesPaneComponent } from './name-types-pane.component';
import { NameTypesResolvePagingParams } from './name-types-resolve-paging-params';

export const nameTypesRoutes: Routes = [
    {
        path: '',
        redirectTo: 'name-types',
        pathMatch: 'full'
    }, {
        path: 'name-types',
        component: NameTypesPaneComponent,
        resolve: {
            'pagingParams': NameTypesResolvePagingParams
        }
    }
];

export const nameTypesPopupRoutes: Routes = [
    {
        path: 'name-type-edit-dialog',
        component: NameTypeEditPopupComponent,
        outlet: 'popup'
    }
];
