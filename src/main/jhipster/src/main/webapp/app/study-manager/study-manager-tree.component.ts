import { Component } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { TreeService } from '../shared/tree/tree.service';
import { StudyTreeService } from '../shared/tree/study/study-tree.service';
import { TreeComponent } from '../shared/tree';
import { AlertService } from '../shared/alert/alert.service';
import { Router } from '@angular/router';

@Component({
    selector: 'jhi-study-tree',
    templateUrl: 'study-manager-tree.component.html',
    providers: [{ provide: TreeService, useClass: StudyTreeService }]
})
export class StudyManagerTreeComponent extends TreeComponent {

    title = 'Browse for studies';

    constructor(public service: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal,
                public router: Router) {
        super(false, 'single', service, activeModal, alertService, translateService, modalService);
    }

    // showSummary() {
    //     const persistPromise = this.persistTreeState();
    //     persistPromise.then(() => {
    //         const selected: TreeComponentResult[] = this.selectedNodes.filter((node: PrimeNgTreeNode) => {
    //             const isFolder = !Boolean(node.leaf);
    //             return this.isFolderSelectionMode ? isFolder : !isFolder;
    //         }).map((node: PrimeNgTreeNode) => {
    //             return <TreeComponentResult>({
    //                 id: node.data.id,
    //                 name: node.data.name,
    //                 showSummary: true
    //             });
    //         });
    //         this.activeModal.close(selected);
    //     });
    //     //
    //     // this.finish();
    //     //
    //     // this.router.navigate(['/study-manager/study/' + ], {queryParams: {}});
    // }

}
