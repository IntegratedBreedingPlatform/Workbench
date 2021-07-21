import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_GERMPLASM_LIST } from '../app.constants';
import { JhiLanguageService } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { GermplasmTreeTableComponent } from '../shared/tree/germplasm/germplasm-tree-table.component';
import { GermplasmList } from './germplasm-list.model';
import { Subscription } from 'rxjs';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-list',
    templateUrl: './germplasm-list.component.html'
})
export class GermplasmListComponent implements OnInit {

    lists: GermplasmList[] = [];

    helpLink: string;
    hideSearchTab = false;

    private listId: number;
    private queryParamSubscription: Subscription;

    constructor(private activatedRoute: ActivatedRoute,
                private paramContext: ParamContext,
                private helpService: HelpService,
                private jhiLanguageService: JhiLanguageService,
                private modalService: NgbModal,
                public activeModal: NgbActiveModal
    ) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = params['listId'];

            if (!this.listId) {
                return;
            }

            if (!this.exists(this.listId)) {
                this.lists.push(new GermplasmList(this.listId, params['listName'], '', true));
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
    }

    setActive(listId: number) {
        this.hideSearchTab = true;

        this.lists.forEach((list: GermplasmList) => {
            list.active = false;
            if (list.id === listId) {
                list.active = true;
            }
        });
    }

    setSearchTabActive() {
        this.hideSearchTab = false;
        this.listId = null;
        this.lists.forEach((list: GermplasmList) => list.active = false);
    }

    closeTab(list: GermplasmList) {
        this.lists.splice(this.lists.indexOf(list), 1);
        if (list.active) {
            this.hideSearchTab = false;
        }
    }

    trackId(index: number, item: GermplasmList) {
        return item.id;
    }

    browseList($event) {
        $event.preventDefault();

        this.modalService.open(GermplasmTreeTableComponent as Component, { size: 'lg', backdrop: 'static' })
            .result.then((germplasmLists) => {
                    if (germplasmLists && germplasmLists.length > 0) {
                        germplasmLists.forEach((germplasmList) => {
                            if (!this.exists(germplasmList.id)) {
                                this.lists.push(new GermplasmList(germplasmList.id, germplasmList.name, '', false));
                            }
                        });

                        this.listId = germplasmLists[germplasmLists.length - 1].id;
                        this.setActive(this.listId);
                    }
                    this.activeModal.close();
                }, () => this.activeModal.dismiss());
    }

    private exists(listId: number) {
        return this.lists.some((list) => list.id === listId);
    }

}
