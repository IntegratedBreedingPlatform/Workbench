import { Routes } from '@angular/router';
import { ReleaseNotesPopupComponent } from './release-notes.component';
import { ReleaseNotesContentComponent } from './release-notes-content.component';

export const RELEASE_NOTES_ROUTES: Routes = [
    {
        path: 'release-notes-dialog',
        component: ReleaseNotesPopupComponent,
        outlet: 'popup',
        children: [
            {
                path: '',
                redirectTo: 'content',
                pathMatch: 'full'
            },
            {
                path: 'content',
                component: ReleaseNotesContentComponent
            }
        ]
    }
];
