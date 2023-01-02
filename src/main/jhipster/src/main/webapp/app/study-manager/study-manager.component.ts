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

@Component({
    selector: 'jhi-studies-manager',
    templateUrl: './study-manager.component.html',
    styleUrls: ['./study-manager.component.scss']
})
export class StudyManagerComponent implements OnInit {

    tabs: NavTab[] = [];

    helpLink: string;
    hideSearchTab = false;

    private studyId: number;
    private queryParamSubscription: Subscription;

    constructor(private paramContext: ParamContext,
                private helpService: HelpService,
                private jhiLanguageService: JhiLanguageService,
                private urlService: UrlService,
                private modalService: NgbModal,
                private activeModal: NgbActiveModal,
                private router: Router,
                private activatedRoute: ActivatedRoute
    ) {
        this.queryParamSubscription = this.activatedRoute.queryParams.subscribe((params) => {
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
                    this.urlService.openStudy(studyId);
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
