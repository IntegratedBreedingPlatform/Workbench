import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Crop } from '../../shared/model/crop.model';

@Injectable()
export class CropService {
    private baseUrl: string = SERVER_API_URL + 'brapi/v1';

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<Crop[]> {
        return this.http.get(`${this.baseUrl}/crops`, { observe: 'response' })
            .pipe(map((response: any) => response.body['result'].data.map((cropName) => new Crop(cropName))));
    }

}
