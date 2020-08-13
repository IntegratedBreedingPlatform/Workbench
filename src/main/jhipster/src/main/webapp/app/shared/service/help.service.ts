import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HELP_MANAGE_SAMPLES } from '../../app.constants';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class HelpService {
    constructor(@Inject(HttpClient) private http: HttpClient) {
    }

    getOnlinHelpLink(): Observable<any> {
        return this.http.get(HELP_MANAGE_SAMPLES, {observe: 'response', responseType: 'text'});
    }
}
