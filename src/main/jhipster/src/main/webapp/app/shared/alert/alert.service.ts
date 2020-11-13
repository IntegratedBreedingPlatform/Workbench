import { Injectable } from '@angular/core';
import { JhiAlertService } from 'ng-jhipster';
import { JhiAlert } from 'ng-jhipster/src/service/alert.service';

@Injectable()
export class AlertService {

    // workaround to avoid auto-close of error messages
    private readonly PERSISTENT_ERROR_TIMEOUT = 1000 * 60 * 60 * 24;

    constructor(private jhiAlertService: JhiAlertService) {
    }

    error(msg: string, params?: any, timeout?: number): JhiAlert {
        this.jhiAlertService.clear();
        return this.jhiAlertService.addAlert({
            msg,
            params,
            type: 'danger',
            timeout: timeout || this.PERSISTENT_ERROR_TIMEOUT,
            toast: true
        }, null);
    }

    success(msg: string, params?: any, timeout?: number): JhiAlert {
        this.jhiAlertService.clear();
        return this.jhiAlertService.addAlert({
            msg,
            params,
            type: 'success',
            timeout,
            toast: true
        }, null);

    }
}
