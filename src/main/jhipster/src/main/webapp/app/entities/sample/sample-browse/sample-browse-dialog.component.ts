import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { TreeNode } from './tree-node.model';
import { SampleTreeService } from './sample-tree.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/observable/forkJoin';

declare var $: any
    , authToken: string
    , selectedProjectId: string
    , loggedInUserId: string;

const TREETABLE_ID = '#treeTable';
const AUTH_PARAMS = {
    authToken,
    selectedProjectId,
    loggedInUserId
};

@Component({
    selector: 'jhi-sample-browse-dialog',
    templateUrl: './sample-browse-dialog.component.html'
})
export class SampleBrowseDialogComponent implements OnInit, OnDestroy, AfterViewInit {

    public nodes: TreeNode[]
     = [{
           'id': "0",
           'parentId': null,
           'isFolder': "1",
           'numOfChildren': 1,
           'name': 'id',
           'owner': 'id',
           'description': 'id',
           'type': 'id',
           'numOfEntries': 1
       }, {
           'id': "1",
           'parentId': "0",
           'isFolder': "0",
           'numOfChildren': 0,
           'name': 'id',
           'owner': 'id',
           'description': 'id',
           'type': 'id',
           'numOfEntries': 0
       }];/**/

    constructor(public activeModal: NgbActiveModal,
                public service: SampleTreeService) {
    }

    ngOnInit(): void {
        /*
        this.treeTable();
        this.service.getInitTree(AUTH_PARAMS).subscribe(
            (res: HttpResponse<TreeNode[]>) => {
                this.nodes = res.body;
                this.nodes.map((node) => node.numOfChildren = 1);
                // expand root nodes TODO use flatMap?
                this.nodes.forEach((node) => {
                    this.service.expand(node.id, AUTH_PARAMS).subscribe(
                        (res: HttpResponse<TreeNode>) => {

                        },
                        (res: HttpErrorResponse) => this.onError(res.message));
                });
            },
            (res: HttpErrorResponse) => this.onError(res.message));
            */
        this.service.getInitTree(AUTH_PARAMS).mergeMap((res: HttpResponse<TreeNode[]>) => {
            this.nodes = res.body;
            this.nodes.forEach(node => node.numOfChildren = 1);
            return Observable.forkJoin(this.nodes.map((node) => this.service.expand(node.id, AUTH_PARAMS)));
        }).subscribe((responses: HttpResponse<TreeNode[]>[]) => {
                responses.forEach((res) => res.body.forEach((node) => this.nodes.push(node)));
                setTimeout(() => this.treeTable(), 0)
            }, (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    ngOnDestroy(): void {
    }

    ngAfterViewInit(): void {
    }

    private treeTable() {
        var component = this;
        $(TREETABLE_ID).treetable({
            expandable: true,
            clickableNodeNames: true,
            initialState: "expanded",
            onNodeCollapse() {
                $(TREETABLE_ID).treetable('unloadBranch', this);
            },
            onNodeExpand() {
                // expandsampleListNode(this);
                component.service.expand(this.id, AUTH_PARAMS).subscribe((res: HttpResponse<TreeNode[]>) => {
                    res.body.forEach((node) => component.nodes.push(node));
                    // setTimeout(component.treeTable.bind(component), 0)
                })
            },
            onInitialized() {
                // initializesampleListTreeTable();
            }
        }, true /* force reinitialization */);
    }

    private onSuccess(data, headers) {
    }

    private onError(error) {

    }

    clear() {
        this.activeModal.dismiss('cancel');
    }
}

@Component({
    selector: 'jhi-sample-browse-popup',
    template: ''
})
export class SampleBrowsePopupComponent implements OnInit, OnDestroy {

    routeSub: any;
    private ngbModalRef: NgbModalRef;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private modalService: NgbModal) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            const modalRef = this.open(SampleBrowseDialogComponent as Component, {size: 'lg', backdrop: 'static'});
            return modalRef;
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

    open(component: Component, id?: number | any): Promise<NgbModalRef> {
        return new Promise<NgbModalRef>((resolve, reject) => {
            const isOpen = this.ngbModalRef !== null;
            if (isOpen) {
                resolve(this.ngbModalRef);
            }

            // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
            setTimeout(() => {
                this.ngbModalRef = this.modalRef(component);
                resolve(this.ngbModalRef);
            }, 0);
        });
    }

    modalRef(component: Component): NgbModalRef {
        const modalRef = this.modalService.open(component, {size: 'lg', backdrop: 'static'});
        modalRef.result.then((result) => {
            this.router.navigate([{outlets: {popup: null}}], {replaceUrl: true, queryParamsHandling: 'merge'});
            this.ngbModalRef = null;
        }, (reason) => {
            this.router.navigate([{outlets: {popup: null}}], {replaceUrl: true, queryParamsHandling: 'merge'});
            this.ngbModalRef = null;
        });
        return modalRef;
    }
}
