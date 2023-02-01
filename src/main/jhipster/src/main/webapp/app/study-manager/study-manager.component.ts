import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_MANAGE_SAMPLES } from '../app.constants';
import { JhiLanguageService } from 'ng-jhipster';
import { UrlService } from '../shared/service/url.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { StudyManagerTreeComponent } from './study-manager-tree.component';
import { NavTab } from '../shared/nav/tab/nav-tab.model';
import { MANAGE_STUDIES_PERMISSIONS } from '../shared/auth/permissions';

@Component({
    selector: 'jhi-studies-manager',
    templateUrl: './study-manager.component.html'
})
export class StudyManagerComponent implements OnInit {

    STUDIES_CREATION_PERMISSIONS = [
        ...MANAGE_STUDIES_PERMISSIONS,
        'MS_MANAGE_OBSERVATION_UNITS',
        'MS_WITHDRAW_INVENTORY',
        'MS_CREATE_PENDING_WITHDRAWALS',
        'MS_CREATE_CONFIRMED_WITHDRAWALS',
        'MS_CANCEL_PENDING_TRANSACTIONS',
        'MS_MANAGE_FILES',
        'MS_CREATE_LOTS'
    ];

    tabs: NavTab[] = [];

    helpLink: string;
    hideSearchTab = false;

    private studyId: number;
    private queryParamSubscription: Subscription;

    constructor(public paramContext: ParamContext,
                public helpService: HelpService,
                public jhiLanguageService: JhiLanguageService,
                public urlService: UrlService,
                public modalService: NgbModal,
                public activeModal: NgbActiveModal,
                public router: Router,
                public activatedRoute: ActivatedRoute
    ) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
            if (params['datasetId']) {
                return;
            }

            this.studyId = parseInt(params['studyId'], 10);
            if (!this.studyId) {
                return;
            }

            if (!this.exists(this.studyId)) {
                this.tabs.push(new NavTab(this.studyId, params['studyName'], true));
            }

            this.setActive(this.studyId);
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

    ngOnInit() {
    }

    browseStudy($event) {
        $event.preventDefault();

        this.modalService.open(StudyManagerTreeComponent, { windowClass: 'modal-medium', backdrop: 'static' }).result.then((studies) => {
            if (studies && studies.length === 1) {
                const study: any = studies[0];
                const studyId: number = study.id;
                if (study.extraParams && study.extraParams.showSummary) {
                    this.router.navigate(['/study-manager/study/' + studyId], { queryParams: { studyId, studyName: study.name } });
                } else {
                    this.urlService.openStudy(studyId, study.name);
                }
            }
            this.activeModal.close();
        }, () => {
        });
    }

    setActive(studyId: number) {
        this.hideSearchTab = true;

        this.tabs.forEach((tab: NavTab) => {
            tab.active = (tab.id === studyId);
        });
    }

    setSearchTabActive() {
        this.hideSearchTab = false;
        this.studyId = null;
        this.tabs.forEach((tab: NavTab) => tab.active = false);
    }

    closeTab(tab: NavTab) {
        this.tabs.splice(this.tabs.indexOf(tab), 1);
        if (tab.active) {
            this.hideSearchTab = false;
        }

        this.router.navigate(['/study-manager'], { queryParams: {} });
    }

    trackId(index: number, item: NavTab) {
        return item.id;
    }

    private exists(studyId: number) {
        return this.tabs.some((tab: NavTab) => tab.id === studyId);
    }

}
