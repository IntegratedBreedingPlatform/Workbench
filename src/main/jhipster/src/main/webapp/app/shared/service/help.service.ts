import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { HELP_BASE_URL } from '../../app.constants';

@Injectable()
export class HelpService {

    constructor(@Inject(HttpClient) private http: HttpClient) {
    }

    getHelpLink(key: string): Observable<any> {
        return this.http.get(HELP_BASE_URL + key, {observe: 'response', responseType: 'text'});
    }

}
