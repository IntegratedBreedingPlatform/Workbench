import { NgModule } from '@angular/core';
import { ReleaseNotesDialogComponent, ReleaseNotesPopupComponent } from './release-notes.component';
import { ReleaseNotesService } from './release-notes.service';
import { RouterModule } from '@angular/router';
import { RELEASE_NOTES_ROUTES } from './release-notes.route';
import { BmsjHipsterSharedModule } from '../shared';
import { ReleaseNotesContentComponent } from './release-notes-content.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(RELEASE_NOTES_ROUTES)
    ],
    declarations: [
        ReleaseNotesDialogComponent,
        ReleaseNotesPopupComponent,
        ReleaseNotesContentComponent
    ],
    entryComponents: [
        ReleaseNotesDialogComponent,
        ReleaseNotesPopupComponent,
        ReleaseNotesContentComponent
    ],
    providers: [ReleaseNotesService]
})
export class ReleaseNotesModule {
}
