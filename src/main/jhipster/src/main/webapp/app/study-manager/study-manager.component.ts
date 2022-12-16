import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_MANAGE_SAMPLES } from '../app.constants';
import { JhiLanguageService } from 'ng-jhipster';
import { UrlService } from '../shared/service/url.service';
import { StudyTreeComponent } from '../shared/tree/study/study-tree.component';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
    selector: 'jhi-studies-manager',
    templateUrl: './study-manager.component.html',
})
export class StudyManagerComponent implements OnInit {

    studies: StudyTab[] = [];

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
                this.studies.push(new StudyTab(this.studyId, params['studyName'], true));
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

        const studyTreeModal = this.modalService.open(StudyTreeComponent, { size: 'lg', backdrop: 'static' });
        studyTreeModal.componentInstance.selectionMode = 'single';
        studyTreeModal.result.then((studyIds) => {
           if (studyIds && studyIds.length === 1) {
               this.urlService.openStudy(studyIds[0].id);
           }
            this.activeModal.close();
        }, () => {});
    }

    setActive(studyId: number) {
        this.hideSearchTab = true;

        this.studies.forEach((study: StudyTab) => {
            study.active = (study.id === studyId);
        });
    }

    setSearchTabActive() {
        this.hideSearchTab = false;
        this.studyId = null;
        this.studies.forEach((study: StudyTab) => study.active = false);
    }

    closeTab(study: StudyTab) {
        this.studies.splice(this.studies.indexOf(study), 1);
        if (study.active) {
            this.hideSearchTab = false;
        }

        this.router.navigate(['/study-manager'], {queryParams: {}});
    }

    trackId(index: number, item: StudyTab) {
        return item.id;
    }

    private exists(studyId: number) {
        return this.studies.some((study: StudyTab) => study.id === studyId);
    }

}

export class StudyTab {
    constructor(
        public id: number,
        public studyName: string,
        public active: boolean
    ) {
    }
}
