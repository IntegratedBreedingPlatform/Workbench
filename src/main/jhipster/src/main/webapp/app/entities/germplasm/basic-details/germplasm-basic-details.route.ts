import { EditGermplasmBasicDetailsPopupComponent } from './germplasm-basic-details-modal.component';
import { Routes } from '@angular/router';

export const germplasmBasicDetailsRoute: Routes = [
    {
        // Path for showing edit germplasm basic details pop-up
        path: 'germplasm-edit-basic-details',
        component: EditGermplasmBasicDetailsPopupComponent,
        outlet: 'popup'
    }];
