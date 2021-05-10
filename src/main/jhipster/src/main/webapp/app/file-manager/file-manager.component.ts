import { Component } from '@angular/core';
import { ActivatedRoute, Route } from '@angular/router';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html'
})
export class FileManagerComponent {
    fileName: string;

    constructor(
        private route: ActivatedRoute
    ) {
        const routeParams = this.route.snapshot.paramMap;
        this.fileName = routeParams.get('fileName');
    }
}
