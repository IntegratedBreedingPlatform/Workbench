import { Component } from '@angular/core';
import { TreeComponent } from '../tree.component';
import { TreeService } from '../tree.service';
import { StudyTreeService } from './study-tree.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../alert/alert.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-study-tree',
    templateUrl: '../tree.component.html',
    providers: [{ provide: TreeService, useClass: StudyTreeService }]
})
export class StudyTreeComponent extends TreeComponent {

    title = 'Browse for studies';

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal) {
        super(false, service, activeModal, alertService, translateService, modalService);
    }
}
