import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { CreateProgramDialogComponent, CreateProgramPopupComponent } from './create-program-dialog.component';
import { ProgramService } from '../../shared/program/service/program.service';
import { CREATE_PRORGAM_ROUTER } from './create-program.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(CREATE_PRORGAM_ROUTER)
    ],
    declarations: [
        CreateProgramDialogComponent,
        CreateProgramPopupComponent,
    ],
    entryComponents: [
        CreateProgramDialogComponent,
        CreateProgramPopupComponent,
    ],
    providers: [
        ProgramService
    ]
})
export class CreateProgramModule {

}
