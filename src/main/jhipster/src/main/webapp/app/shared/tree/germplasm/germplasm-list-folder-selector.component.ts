import { Component } from '@angular/core';
import { TreeService } from '../tree.service';
import { GermplasmTreeService } from './germplasm-tree.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../alert/alert.service';
import { TranslateService } from '@ngx-translate/core';
import { TreeComponent } from '../tree.component';

@Component({
    selector: 'jhi-germplasm-list-folder-selector',
    templateUrl: '../tree-table.component.html',
    providers: [{ provide: TreeService, useClass: GermplasmTreeService }]
})
export class GermplasmListFolderSelectorComponent extends TreeComponent {

    title = 'Select folder';

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal) {
        super(false, 'single', service, activeModal, alertService, translateService, modalService);
        this.isFolderSelectionMode = true;
    }
}
