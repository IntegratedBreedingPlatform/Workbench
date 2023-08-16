import {Component, OnInit} from '@angular/core';
import {NavTab} from '../shared/nav/tab/nav-tab.model';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {ParamContext} from '../shared/service/param.context';
import {HelpService} from '../shared/service/help.service';
import {JhiEventManager, JhiLanguageService} from 'ng-jhipster';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AlertService} from '../shared/alert/alert.service';
import {HELP_GERMPLASM_LIST} from '../app.constants';
import {HttpErrorResponse} from '@angular/common/http';
import {formatErrorList} from '../shared/alert/format-error-list';

@Component({
    selector: 'jhi-cross-plan-manager',
    templateUrl: './cross-plan-manager.component.html'
})
export class CrossPlanManagerComponent implements OnInit {

    tabs: NavTab[] = [];

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
                private alertService: AlertService,
    ) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            this.listId = parseInt(params['listId'], 10);

            if (!this.listId) {
                return;
            }

            if (!this.exists(this.listId)) {
                this.tabs.push(new NavTab(this.listId, params['listName'], true));
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

    ngOnInit(): void {
    }

    private exists(listId: number) {
        return this.tabs.some((tab: NavTab) => tab.id === listId);
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', {param: msg});
        } else {
            this.alertService.error('error.general');
        }
    }

    browseList($event) {

    }

    setActive(listId: number) {

    }

    closeTab(tab: NavTab) {

    }

    setSearchTabActive() {

    }

    trackId(index: number, item: NavTab) {
        return item.id;
    }
}
