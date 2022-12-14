import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { HelpService } from '../shared/service/help.service';
import { HELP_MANAGE_SAMPLES } from '../app.constants';
import { JhiLanguageService } from 'ng-jhipster';
import { UrlService } from '../shared/service/url.service';
import { StudyTreeComponent } from '../shared/tree/study/study-tree.component';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-studies-manager',
    templateUrl: './study-manager.component.html',
})
export class StudyManagerComponent implements OnInit {

    helpLink: string;

    constructor(private paramContext: ParamContext,
                private helpService: HelpService,
                private jhiLanguageService: JhiLanguageService,
                private urlService: UrlService,
                private modalService: NgbModal,
                private activeModal: NgbActiveModal
    ) {
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

}
