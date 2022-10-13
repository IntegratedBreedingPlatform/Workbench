import { Component, OnDestroy, OnInit } from '@angular/core';
import { SampleList } from './sample-list.model';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { SampleContext } from './sample.context';
import { HelpService } from '../../shared/service/help.service';
import { HELP_MANAGE_SAMPLES } from '../../app.constants';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SampleSearchListComponent } from './sample-search-list.component';
import { TreeTableComponent } from './tree-table';
import { ListBuilderContext } from '../../shared/list-builder/list-builder.context';
import { ListBuilderService } from '../../shared/list-creation/service/list-builder.service';
import { SampleListBuilderService } from '../../shared/list-creation/service/sample-list-builder.service';
import { ParamContext } from '../../shared/service/param.context';

declare var $: any;

@Component({
    selector: 'jhi-sample-manager',
    templateUrl: './sample-manager.component.html',
    providers: [
        { provide: ListBuilderService, useClass: SampleListBuilderService },
    ],
})
export class SampleManagerComponent implements OnInit, OnDestroy {

    private listId: number;
    private queryParamSubscription: Subscription;
    private paramSubscription: Subscription;
    helpLink: string;
    predicate: any;

    lists: SampleList[] = [];

    constructor(private activatedRoute: ActivatedRoute,
                private modalService: NgbModal,
                public activeModal: NgbActiveModal,
                public listBuilderContext: ListBuilderContext,
                private router: Router,
                private sampleContext: SampleContext,
                private helpService: HelpService,
                private paramContext: ParamContext
                ) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = params['listId'];

            if (!this.listId) {
                return;
            }

            if (!this.exists(this.listId)) {
                this.lists.push(new SampleList(this.listId, 'List ' + this.listId, '', true, []));
            }

            this.setActive(this.listId);
        });
        this.paramContext.readParams();

        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_MANAGE_SAMPLES).toPromise().then((response) => {
                if (response.body) {
                    this.helpLink = response.body;
                }
            }).catch((error) => {
            });
        }
    }

    closeTab(list: SampleList) {
        this.lists.splice(this.lists.indexOf(list), 1);
        if (list.active) {
            const first = this.lists[0];
            if (first) {
                first.active = true;
                this.navigate(first.id);
            } else {
                this.navigate('');
            }
        }
    }

    openSearch($event) {
        $event.preventDefault();
        const confirmModalRef = this.modalService.open(SampleSearchListComponent as Component, { size: 'lg', backdrop: 'static' });
        confirmModalRef.result.then(() => {
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
    }

    private setActive(listId: number) {
        this.lists.forEach((list) => {
            list.active = false;
            if (list.id === listId) {
                list.active = true;
                this.sampleContext.activeList = list;
            }
        });
    }

    private exists(listId: number) {
        return this.lists.some((list) => list.id === listId);
    }

    private navigate(listId: any) {
        this.listId = listId;
        this.router.navigate(['/sample-manager'], {
            queryParams: {
                listId: this.listId
            }
        });
    }

    trackId(index: number, item: SampleList) {
        return item.id;
    }

    ngOnInit() {
    }

    ngOnDestroy(): void {
        this.paramSubscription.unsubscribe();
        this.queryParamSubscription.unsubscribe();
    }

    browseList($event) {
        $event.preventDefault();
        const confirmModalRef = this.modalService.open(TreeTableComponent as Component, { size: 'lg', backdrop: 'static' });
        confirmModalRef.result.then(() => {
            this.activeModal.close();
        }, () => this.activeModal.dismiss());
    }
}
