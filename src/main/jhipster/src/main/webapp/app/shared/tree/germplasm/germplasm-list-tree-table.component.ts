import { Component } from '@angular/core';
import { TreeComponent } from '../tree.component';
import { TreeService } from '../tree.service';
import { GermplasmTreeService } from './germplasm-tree.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../alert/alert.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-germplasm-tree-table',
    templateUrl: '../tree-table.component.html',
    providers: [{ provide: TreeService, useClass: GermplasmTreeService }]
})
export class GermplasmListTreeTableComponent extends TreeComponent {

    title = 'Browse for lists';

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal) {
        super(false, 'multiple', service, activeModal, alertService, translateService, modalService);
    }
}
