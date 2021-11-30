import { ListMetadataComponent } from '../shared/list-creation/list-metadata.component';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ListService } from '../shared/list-creation/service/list.service';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ParamContext } from '../shared/service/param.context';
import { AlertService } from '../shared/alert/alert.service';
import { DateHelperService } from '../shared/service/date.helper.service';

@Component({
    selector: 'jhi-germplasm-list-metadata',
    templateUrl: '../shared/list-creation/list-metadata.component.html',
    providers: [
        { provide: ListService, useClass: GermplasmListService },
    ],
})
export class GermplasmListMetadataComponent extends ListMetadataComponent {

    constructor(public modal: NgbActiveModal,
                public eventManager: JhiEventManager,
                public paramContext: ParamContext,
                public alertService: AlertService,
                public listService: ListService,
                public dateHelperService: DateHelperService) {
        super(modal, eventManager, paramContext, alertService, listService, dateHelperService);
    }

}
