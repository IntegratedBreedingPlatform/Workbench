import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { RoleType } from '../models/role-type.model';
import { Crop } from '../../shared/model/crop.model';

@Injectable()
export class CropService {
    private baseUrl: string = SERVER_API_URL + 'brapi/v1';
    public crops: Crop[];

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<Crop[]> {
        return this.http.get(`${this.baseUrl}/crops`, { observe: 'response' }).pipe(map((response: HttpResponse<Crop[]>) => response.body));
    }

}

function mapCrops(crops: Crop[]) {
    return crops.map(toCrop);
}

function toCrop(r: any): Crop {
    return {
        cropName: r
    };
}
