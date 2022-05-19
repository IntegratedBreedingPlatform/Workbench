import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { CropGenotypingParameter } from '../model/crop-genotyping-parameter';

@Injectable()
export class CropGenotypingParameterService {
    constructor(private http: HttpClient) {
    }

    getByCropName(cropName: string): Observable<CropGenotypingParameter> {
        return this.http.get<CropGenotypingParameter>(SERVER_API_URL + `crops/${cropName}/crop-genotyping-parameter`);
    }

    getToken(tokenEndpoint: string, userName: string, password: string): Observable<any> {
        return this.http.post(tokenEndpoint, { username: userName, password: password });
    }
}
