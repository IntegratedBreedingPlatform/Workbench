import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_GERMPLASM_LIST } from '../app.constants';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListTreeTableComponent } from '../shared/tree/germplasm/germplasm-list-tree-table.component';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { GermplasmListModel } from '../shared/germplasm-list/model/germplasm-list.model';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { GermplasmListManagerContext } from './germplasm-list-manager.context';

@Component({
    selector: 'jhi-germplasm-list',
    templateUrl: './germplasm-list.component.html'
})
export class GermplasmListComponent implements OnInit {

    lists: GermplasmListTab[] = [];

    helpLink: string;
    hideSearchTab = false;

    private listId: number;
    private queryParamSubscription: Subscription;
    eventSubscriber: Subscription;

    constructor(private activatedRoute: ActivatedRoute,
                private paramContext: ParamContext,
                private helpService: HelpService,
                private jhiLanguageService: JhiLanguageService,
                private modalService: NgbModal,
                private activeModal: NgbActiveModal,
                private router: Router,
                private eventManager: JhiEventManager,
                private germplasmListService: GermplasmListService,
                private alertService: AlertService,
                private germplasmListManagerContext: GermplasmListManagerContext
    ) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = parseInt(params['listId'], 10);

            if (!this.listId) {
                return;
            }

            if (!this.exists(this.listId)) {
                this.lists.push(new GermplasmListTab(this.listId, params['listName'], true));
            }

            this.setActive(this.listId);
        });

        this.paramContext.readParams();
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_GERMPLASM_LIST).toPromise().then((response) => {
                if (response.body) {
                    this.helpLink = response.body;
                }
            }).catch((error) => {
            });
        }
    }

    ngOnInit() {
        this.registerEvents();
    }

    registerEvents() {
        this.eventSubscriber = this.eventManager.subscribe('germplasmListDeleted', (event) => {
            this.lists.forEach((list: GermplasmListTab) => {
                if (event.content === list.id) {
                    this.closeTab(list);
                }
            });
            this.setSearchTabActive();
        });

        this.eventSubscriber = this.eventManager.subscribe('listMetadataUpdated', (event) => {
            this.lists.forEach((list: GermplasmListTab) => {
                if (event.content === list.id) {
                    this.germplasmListService.getGermplasmListById(list.id).subscribe(
                        (res: HttpResponse<GermplasmListModel>) => list.listName = res.body.listName,
                        (res: HttpErrorResponse) => this.onError(res)
                    );
                }
            });
        });
    }

    setActive(listId: number) {
        this.hideSearchTab = true;

        this.lists.forEach((list: GermplasmListTab) => {
            list.active = (list.id === listId);
        });
        this.germplasmListManagerContext.activeGermplasmListId = listId;
    }

    setSearchTabActive() {
        this.hideSearchTab = false;
        this.listId = null;
        this.lists.forEach((list: GermplasmListTab) => list.active = false);
        this.germplasmListManagerContext.activeGermplasmListId = null;
    }

    closeTab(list: GermplasmListTab) {
        this.lists.splice(this.lists.indexOf(list), 1);
        if (list.active) {
            this.hideSearchTab = false;
        }

        this.router.navigate(['/germplasm-list'], {queryParams: {}});
    }

    trackId(index: number, item: GermplasmListTab) {
        return item.id;
    }

    browseList($event) {
        $event.preventDefault();

        this.modalService.open(GermplasmListTreeTableComponent as Component, { size: 'lg', backdrop: 'static' })
            .result.then((germplasmLists) => {
                    if (germplasmLists && germplasmLists.length > 0) {
                        germplasmLists.forEach((germplasmList) => {
                            if (!this.exists(germplasmList.id)) {
                                this.lists.push(new GermplasmListTab(germplasmList.id, germplasmList.name, false));
                            }
                        });

                        this.listId = germplasmLists[germplasmLists.length - 1].id;
                        this.setActive(this.listId);
                        this.router.navigate([`/germplasm-list/list/${this.listId}`], { queryParams: { listId: this.listId } });
                    }
                    this.activeModal.close();
                }, () => this.activeModal.dismiss());
    }

    private exists(listId: number) {
        return this.lists.some((list) => list.id === listId);
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

}

export class GermplasmListTab {
    constructor(
        public id: number,
        public listName: string,
        public active: boolean
    ) {
    }
}
