import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_GERMPLASM_LIST } from '../app.constants';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { ActivatedRoute, Router } from '@angular/router';
import { GermplasmTreeTableComponent } from '../shared/tree/germplasm/germplasm-tree-table.component';
import { Subscription } from 'rxjs';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

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
                private eventManager: JhiEventManager
    ) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = params['listId'];

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
        this.registerDeleteGermplasmList();
    }

    registerDeleteGermplasmList() {
        this.eventSubscriber = this.eventManager.subscribe('germplasmListDeleted', (event) => {
            this.lists.forEach((list: GermplasmListTab) => {
                if (event.content === list.id) {
                    this.closeTab(list);
                }
            });
            this.setSearchTabActive();
        });
    }

    setActive(listId: number) {
        this.hideSearchTab = true;

        this.lists.forEach((list: GermplasmListTab) => {
            list.active = (list.id === listId) ? true : false;
        });
    }

    setSearchTabActive() {
        this.hideSearchTab = false;
        this.listId = null;
        this.lists.forEach((list: GermplasmListTab) => list.active = false);
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

        this.modalService.open(GermplasmTreeTableComponent as Component, { size: 'lg', backdrop: 'static' })
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

}

export class GermplasmListTab {
    constructor(
        public id: number,
        public listName: string,
        public active: boolean
    ) {
    }
}
