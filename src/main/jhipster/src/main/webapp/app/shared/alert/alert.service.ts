import {Injectable} from '@angular/core';
import {JhiAlert, JhiAlertService} from 'ng-jhipster';

@Injectable()
export class AlertService {

    constructor(private jhiAlertService: JhiAlertService) {
    }

    // wrapper so that error() won't close automatically
    error(msg: string, params?: any, timeout?: number): JhiAlert {
        return this.jhiAlertService.addAlert({
            msg,
            params,
            type: 'danger',
            timeout,
            toast: true
        }, null);
    }

    success(msg: string, params?: any, timeout?: number): JhiAlert {
        return this.jhiAlertService.addAlert({
            msg,
            params,
            type: 'success',
            timeout: timeout || 5000,
            toast: true
        }, null);

    }

    warning(msg: string, params?: any, timeout?: number): JhiAlert {
        return this.jhiAlertService.addAlert({
            msg,
            params,
            type: 'warning',
            timeout,
            toast: true
        }, null);
    }
}
