import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class HelpService {

    private readonly HELP_BASE_URL: string;

    constructor(@Inject(HttpClient) private http: HttpClient) {
        this.HELP_BASE_URL = '/ibpworkbench/controller/help/getUrl/';
    }

    getHelpLink(key: string): Observable<any> {
        return this.http.get(this.HELP_BASE_URL + key, {observe: 'response', responseType: 'text'});
    }

}
