import { Component } from '@angular/core';
import { TreeComponent } from '../tree.component';
import { TreeService } from '../tree.service';
import { GermplasmTreeTableService } from './germplasm-tree-table.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-tree-table',
    templateUrl: '../tree-table.component.html',
    providers: [{ provide: TreeService, useClass: GermplasmTreeTableService }]
})
export class GermplasmTreeTableComponent extends TreeComponent {

    title = 'Browse for lists';

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal) {
        super(service, activeModal);
    }
}
