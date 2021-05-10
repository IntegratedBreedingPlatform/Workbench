import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { FileManagerComponent } from './file-manager.component';
import { RouterModule } from '@angular/router';
import { fileManagerRoutes } from './file-manager.route';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(fileManagerRoutes)
    ],
    declarations: [
        FileManagerComponent
    ]
})
export class FileManagerModule {}
