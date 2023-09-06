import { Component, OnInit } from '@angular/core';
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
import { Principal } from '../shared';

@Component({
    selector: 'jhi-study-tree',
    templateUrl: 'study-manager-tree.component.html',
    providers: [{ provide: TreeService, useClass: StudyTreeService }]
})
export class StudyManagerTreeComponent extends TreeComponent implements OnInit {

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

    user?: any;

    constructor(public treeService: TreeService,
                public activeModal: NgbActiveModal,
                public alertService: AlertService,
                public translateService: TranslateService,
                public modalService: NgbModal,
                public router: Router,
                private principal: Principal) {
        super(false, 'single', treeService, activeModal, alertService, translateService, modalService,
            ['Studies', 'Templates']);
    }

    async ngOnInit() {
        this.user = await this.principal.identity();

        super.ngOnInit();
    }

    onNodeDrop(event, source: PrimeNgTreeNode, target: PrimeNgTreeNode) {
        // Prevent to move source on same parent folder
        if (source.parent.data.id === target.data.id) {
            return;
        }

        if (!this.validateStudyIsLocked(source)) {
            return;
        }

        // If node/study has no programUUID, it means it is a templates folder/study.
        if (!source.data.programUUID) {
            // Prevent templates from moving
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.templates.move.not.allowed');
            return;
        }

        // Prevent to move source if parent has a child with same name as the source
        if (event.dropNode.children && event.dropNode.children.find((node) => node.data.name === source.data.name)) {
            this.alertService.error('bmsjHipsterApp.tree-table.messages.folder.move.parent.duplicated.name');
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
                ownerUserName: node.ownerUserName || '',
                ownerId: node.ownerId,
                isLocked: node.isLocked || '',
                description: node.description || (parent && '-'), // omit for root folders
                programUUID: node.programUUID
            },
            draggable: true,
            droppable: true,
            selectable: true,
            leaf: !node.isFolder,
            parent,
        };
    }

    viewSummary(extraParams?: any) {
        if (!this.validateSelectedNodeIsStudy()) {
            return;
        }
        super.finish(extraParams);
    }

    open() {
        if (!this.validateSelectedNodeIsStudy()) {
            return;
        }

        if (!this.validateStudyIsLocked(this.selectedNodes[0])) {
            return;
        }

        super.finish();
    }

    private validateSelectedNodeIsStudy(): boolean {
        if (!this.selectedNodes || this.selectedNodes.length === 0 || !this.selectedNodes[0].leaf) {
            this.alertService.error('study.manager.tree.error.select-study');
            return false;
        }
        return true;
    }

    private validateStudyIsLocked(treeNode: PrimeNgTreeNode): boolean {
        if (treeNode.data.isLocked &&
            this.user && !this.user.userRoles.some((userRole) => userRole.role.name === 'SuperAdmin') && treeNode.data.ownerId !== this.user.id.toString()) {
            this.alertService.error('study.manager.errors.study-locked', { ownerName: treeNode.data.ownerUserName });
            return false;
        }
        return true;
    }

}
