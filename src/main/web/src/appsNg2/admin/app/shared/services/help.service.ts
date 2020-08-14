import { Inject, Injectable } from '@angular/core';
import { Http, ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs';
import { HELP_SITE_ADMINISTRATION } from '../../app.constants';
import ServiceHelper from './service.helper';

@Injectable()
export class HelpService {
    constructor(@Inject(Http) private http:Http) {
    }

    getOnlinHelpLink(): Observable<any> {
        return this.http.get(HELP_SITE_ADMINISTRATION, {responseType: ResponseContentType.Text});
    }
}