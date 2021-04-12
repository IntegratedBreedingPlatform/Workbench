import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ReleaseNotesService } from './release-notes.service';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ReleaseNotes } from './release-notes.model';

@Component({
    selector: 'jhi-release-notes-content',
    templateUrl: './release-notes-content.component.html',
})
export class ReleaseNotesContentComponent implements OnInit {

    content: SafeHtml;

    constructor(private sanitizer: DomSanitizer,
                private releaseNoteService: ReleaseNotesService,
                private http: HttpClient) {
    }

    ngOnInit(): void {
        this.releaseNoteService.getLatest().subscribe((resp: HttpResponse<ReleaseNotes>) => {
            this.releaseNoteService.getContent(resp.body.version).subscribe((resp1) => this.content = resp1);
        });
    }

}
