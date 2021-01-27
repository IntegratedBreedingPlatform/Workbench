import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { Tool } from '../model/tool.model';

@Injectable()
export class ToolService {
    constructor(private http: HttpClient) {
    }

    getTools(cropName: string, programId: number): Observable<HttpResponse<Tool[]>> {
        const params = {
            cropName,
            programId: programId.toString()
        };
        return this.http.get<Tool[]>(SERVER_API_URL + `tools`, { params, observe: 'response' });
    }

}
