import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ReleaseNotesService } from './release-notes.service';
import { ActivatedRoute } from '@angular/router';
import { ReleaseNoteContext } from './release-note.context';

@Component({
    selector: 'jhi-release-notes-content',
    templateUrl: './release-notes-content.component.html',
})
export class ReleaseNotesContentComponent implements OnInit {

    fileName: string;
    content: SafeHtml;

    constructor(private activatedRoute: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private releaseNoteService: ReleaseNotesService,
                private paramContext: ReleaseNoteContext) {
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.fileName = (queryParams.fileName) ? queryParams.fileName : this.paramContext.fileName;
    }

    ngOnInit(): void {
        this.releaseNoteService.getContent(this.fileName).subscribe((resp1) => this.content = resp1);
    }

}
