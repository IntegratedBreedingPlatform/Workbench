import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { CropGenotypingParameter } from '../model/crop-genotyping-parameter';

@Injectable()
export class CropGenotypingParameterService {
    constructor(private http: HttpClient) {
    }

    getByCropName(cropName: string): Observable<CropGenotypingParameter> {
        return this.http.get<CropGenotypingParameter>(SERVER_API_URL + `crops/${cropName}/crop-genotyping-parameters`);
    }

    getToken(cropName: string): Observable<string> {
        const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
        return this.http.get<string>(SERVER_API_URL + `crops/${cropName}/crop-genotyping-parameters/token`, { headers, responseType: 'text' as 'json' });
    }
}
