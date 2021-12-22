import { AboutComponent } from './about.component';
import { Router, Routes } from '@angular/router';
import { FileManagerComponent } from '../file-manager/file-manager.component';

export const aboutRoutes: Routes = [{
    path: 'about',
    component: AboutComponent
}]
