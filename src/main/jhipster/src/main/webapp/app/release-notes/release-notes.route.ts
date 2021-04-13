import { Routes } from '@angular/router';
import { ReleaseNotesDialogComponent, ReleaseNotesPopupComponent } from './release-notes.component';
import { ReleaseNotesContentComponent } from './release-notes-content.component';

export const RELEASE_NOTES_ROUTES: Routes = [
    {
        path: 'release-notes-popup',
        component: ReleaseNotesPopupComponent,
        outlet: 'popup',
    },
    {
        path: 'release-notes-dialog',
        component: ReleaseNotesDialogComponent,
        children: [
            {
                path: '',
                redirectTo: 'content',
                pathMatch: 'full',
            },
            {
                path: 'content',
                component: ReleaseNotesContentComponent,
            },
            {
                path: 'coming-soon',
                component: ReleaseNotesContentComponent,
            }
        ]
    }
];
