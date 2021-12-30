import { Routes } from '@angular/router';
import { CreateProgramPopupComponent } from './create-program-dialog.component';

export const CREATE_PRORGAM_ROUTER: Routes =  [
    {
        path: 'create-program',
        component: CreateProgramPopupComponent,
        outlet: 'popup'
    }
]
