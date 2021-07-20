import { Routes } from '@angular/router';
import { FileManagerComponent } from './file-manager.component';

export const fileManagerRoutes: Routes = [{
    path: 'file-manager/:filePath',
    component: FileManagerComponent
}]
