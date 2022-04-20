import { NgModule } from '@angular/core';
import { CopMatrixComponent, CopMatrixPopupComponent } from './cop-matrix.component';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { copRoutes } from './cop.route';
import { CopService } from './cop.service';
import { BtypeSelectorModalComponent } from './btype-selector-modal.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(copRoutes)
    ],
    declarations: [
        CopMatrixComponent,
        CopMatrixPopupComponent,
        BtypeSelectorModalComponent
    ],
    entryComponents: [
        CopMatrixComponent,
        CopMatrixPopupComponent,
        BtypeSelectorModalComponent
    ],
    providers: [
        CopService
    ],
    exports: [
        CopMatrixComponent
    ]
})
export class CopModule {
}
