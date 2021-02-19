import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ElementRef, NgModule, Renderer } from '@angular/core';
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

@NgModule({
    providers: [
        DatePipe,
        JhiDataUtils,
        JhiDateUtils,
        JhiParseLinks,
        {
            provide: JhiLanguageService,
            useClass: MockLanguageService
        },
        {
            provide: JhiLanguageHelper,
            useClass: MockLanguageHelper
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
            provide: Renderer,
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
