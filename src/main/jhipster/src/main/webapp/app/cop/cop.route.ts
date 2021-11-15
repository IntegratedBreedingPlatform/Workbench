import { Routes } from '@angular/router';
import { CopMatrixPopupComponent } from './cop-matrix.component';

export const copRoutes: Routes = [{
    path: 'cop-matrix',
    component: CopMatrixPopupComponent,
    outlet: 'popup'
}];
