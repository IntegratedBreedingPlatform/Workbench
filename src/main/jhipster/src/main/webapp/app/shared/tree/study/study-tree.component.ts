import { Component } from '@angular/core';
import { TreeComponent } from '../tree.component';
import { TreeService } from '../tree.service';
import { StudyTreeService } from './study-tree.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-study-tree',
    templateUrl: '../tree.component.html',
    providers: [{ provide: TreeService, useClass: StudyTreeService }]
})
export class StudyTreeComponent extends TreeComponent {

    title = 'Browse for studies';

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal) {
        super(service, activeModal);
    }
}
