import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Program } from '../../program/model/program';
import { SERVER_API_URL } from '../../../app.constants';
import { createRequestOption } from '../..';

@Injectable()
export class CropService {
    constructor(private http: HttpClient) {
    }

    getCrops(): Observable<string[]> {
        return this.http.get<string[]>(SERVER_API_URL + 'crop/list');
    }
}
