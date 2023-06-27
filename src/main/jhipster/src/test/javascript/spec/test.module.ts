import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ElementRef, NgModule, Renderer2 } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiAlertService, JhiDataUtils, JhiDateUtils, JhiEventManager, JhiLanguageService, JhiParseLinks } from 'ng-jhipster';

import { MockLanguageHelper, MockLanguageService } from './helpers/mock-language.service';
import { JhiLanguageHelper } from '../../../main/webapp/app/shared';
import { MockActivatedRoute, MockRouter } from './helpers/mock-route.service';
import { MockActiveModal } from './helpers/mock-active-modal.service';
import { MockEventManager } from './helpers/mock-event-manager.service';
import { MockAlertService, MockJhiAlertService } from './helpers/mock-alert.service';
import { AlertService } from '../../../main/webapp/app/shared/alert/alert.service';
import {ParamContext} from '../../../main/webapp/app/shared/service/param.context';
import { TranslateService } from '@ngx-translate/core';
import { MockTranslateService } from './helpers/mock-translate.service';

@NgModule({
    providers: [
        DatePipe,
        JhiDataUtils,
        JhiDateUtils,
        JhiParseLinks,
        ParamContext,
        {
            provide: JhiLanguageService,
            useClass: MockLanguageService
        },
        {
            provide: JhiLanguageHelper,
            useClass: MockLanguageHelper
        },
        {
            provide: TranslateService,
            useClass: MockTranslateService
        },
        {
            provide: JhiEventManager,
            useClass:  MockEventManager
        },
        {
            provide: NgbActiveModal,
            useClass: MockActiveModal
        },
        {
            provide: ActivatedRoute,
            useValue: new MockActivatedRoute({id: 123})
        },
        {
            provide: Router,
            useClass: MockRouter
        },
        {
            provide: ElementRef,
            useValue: null
        },
        {
            provide: Renderer2,
            useValue: null
        },
        {
            provide: JhiAlertService,
            useValue: new MockJhiAlertService()
        },
        {
            provide: AlertService,
            useValue: new MockAlertService()
        }
    ],
    imports: [HttpClientTestingModule]
})
export class BmsjHipsterTestModule {}

export const cropName = 'maize';
