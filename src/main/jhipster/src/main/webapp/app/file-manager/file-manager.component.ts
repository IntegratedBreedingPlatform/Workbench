import { Component } from '@angular/core';
import { ActivatedRoute, Route } from '@angular/router';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html'
})
export class FileManagerComponent {
    fileKey: string;

    constructor(
        private route: ActivatedRoute
    ) {
        const routeParams = this.route.snapshot.paramMap;
        this.fileKey = routeParams.get('fileKey');
    }
}
