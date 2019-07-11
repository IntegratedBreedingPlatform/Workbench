import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import ServiceHelper from './service.helper';
import { Observable } from 'rxjs';
import { Crop } from '../models/crop.model';

@Injectable()
export class CropService {
    private baseUrl: string = '/bmsapi/brapi/v1';

    constructor(private http: Http) {
    }

    getAll(): Observable<Crop[]> {
        return this.http.get(`${this.baseUrl}/crops`, { headers: this.getHeaders() })
            .map((response: any) => mapCrops(response.json().result.data));
    }

    private getHeaders() {
        return ServiceHelper.getBrApiHeaders();
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
