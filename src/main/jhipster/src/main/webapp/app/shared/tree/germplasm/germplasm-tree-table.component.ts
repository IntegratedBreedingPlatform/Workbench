import { Component } from '@angular/core';
import { TreeComponent } from '../tree.component';
import { TreeService } from '../tree.service';
import { GermplasmTreeService } from './germplasm-tree.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../alert/alert.service';
import { TranslateService } from '@ngx-translate/core';

// TODO: remove it when user would be able to create/rename/delete folders. Instead use GermplasmListTreeTableComponent
@Component({
    selector: 'jhi-germplasm-tree-table',
    templateUrl: '../tree-table.component.html',
    providers: [{ provide: TreeService, useClass: GermplasmTreeService }]
})
export class GermplasmTreeTableComponent extends TreeComponent {

    title = 'Browse for lists';

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal) {
        super(true, 'multiple', service, activeModal, alertService, translateService, modalService);
    }
}
