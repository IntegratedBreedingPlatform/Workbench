import { Routes } from '@angular/router';
import { CreateProgramPopupComponent } from './create-program-dialog.component';
import { ADD_PROGRAM_PERMISSION } from '../../shared/auth/permissions';
import { RouteAccessService } from '../../shared';

export const CREATE_PRORGAM_ROUTER: Routes =  [
    {
        path: 'create-program',
        component: CreateProgramPopupComponent,
        outlet: 'popup',
        data: { authorities: [...ADD_PROGRAM_PERMISSION] },
        canActivate: [RouteAccessService]
    }
]
