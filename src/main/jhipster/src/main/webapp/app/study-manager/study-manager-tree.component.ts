import { Component } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { TreeService } from '../shared/tree/tree.service';
import { StudyTreeService } from '../shared/tree/study/study-tree.service';
import { TreeComponent, TreeNode } from '../shared/tree';
import { AlertService } from '../shared/alert/alert.service';
import { Router } from '@angular/router';
import { TreeNode as PrimeNgTreeNode } from 'primeng/components/common/treenode';
import { HttpErrorResponse } from '@angular/common/http';
import { MANAGE_STUDIES_PERMISSIONS } from '../shared/auth/permissions';

@Component({
    selector: 'jhi-study-tree',
    templateUrl: 'study-manager-tree.component.html',
    providers: [{ provide: TreeService, useClass: StudyTreeService }]
})
export class StudyManagerTreeComponent extends TreeComponent {

    STUDIES_EDITION_PERMISSIONS = [
        ...MANAGE_STUDIES_PERMISSIONS,
        'MS_MANAGE_OBSERVATION_UNITS',
        'MS_WITHDRAW_INVENTORY',
        'MS_CREATE_PENDING_WITHDRAWALS',
        'MS_CREATE_CONFIRMED_WITHDRAWALS',
        'MS_CANCEL_PENDING_TRANSACTIONS',
        'MS_MANAGE_FILES',
        'MS_CREATE_LOTS'
    ];

    title = 'Browse for studies';

    constructor(public treeService: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal,
                public router: Router) {
        super(false, 'single', treeService, activeModal, alertService, translateService, modalService);
    }

    onNodeDrop(event, source: PrimeNgTreeNode, target: PrimeNgTreeNode) {
        // Prevent to move source on same parent folder
        if (source.parent.data.id === target.data.id) {
            return;
        }

        // Prevent to move source if parent has a child with same name as the source
        if (event.dropNode.children && event.dropNode.children.find((node) => node.data.name === source.data.name)) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.parent.duplicated.name');
            return;
        }

        if (source.children && source.children.length !== 0) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.cannot.move.has.children',
                { folder: source.data.name });
            return;
        }

        if (target.leaf) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.not.allowed');
            return;
        }

        event.accept();

        this.treeService.move(source.data.id, target.data.id).subscribe(
            (res: TreeNode) => {
                this.expand(target);
            },
            (res: HttpErrorResponse) => {
                // TODO: FIX ME! Due to primeng7 does not support accepting the event within subscribe, we are handling the re-render of the component by calling the expand method.
                // Check issue reported: https://github.com/primefaces/primeng/issues/7386
                this.expand(source.parent);
                this.expand(target);
                this.alertService.error('bmsjHipsterApp.tree-table.messages.error', { param: res.error.errors[0].message })
            });
    }

    toPrimeNgNode(node: TreeNode, parent?: PrimeNgTreeNode): PrimeNgTreeNode {
        return {
            label: node.name,
            data: {
                id: node.key,
                name: node.name || '',
                owner: node.owner || '',
                isLocked: node.isLocked || '',
                description: node.description || (parent && '-') // omit for root folders
            },
            draggable: true,
            droppable: true,
            selectable: true,
            leaf: !node.isFolder,
            parent,
        };
    }

    finish(extraParams?: any) {
        if (!this.selectedNodes || this.selectedNodes.length === 0 || !this.selectedNodes[0].leaf) {
            this.alertService.error('study.manager.tree.error.select-study');
            return;
        }
        super.finish(extraParams);
    }

}
