import { NgModule } from '@angular/core';
import { CopMatrixComponent, CopMatrixPopupComponent } from './cop-matrix.component';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { copRoutes } from './cop.route';
import { CopService } from './cop.service';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(copRoutes)
    ],
    declarations: [
        CopMatrixComponent,
        CopMatrixPopupComponent
    ],
    entryComponents: [
        CopMatrixComponent,
        CopMatrixPopupComponent
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
