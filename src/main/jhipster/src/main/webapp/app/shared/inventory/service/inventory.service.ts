import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Location } from '../../model/location.model';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { InventoryUnit } from '../model/inventory-unit.model';

@Injectable()
export class InventoryService {

    constructor(private http: HttpClient,
                private paramContext: ParamContext,
                private router: Router) {
    }

    queryUnits(): Observable<InventoryUnit[]> {
        const cropName = this.paramContext.cropName;
        return this.http.get<InventoryUnit[]>(SERVER_API_URL + `crops/${cropName}/inventory-units`,
            { observe: 'response' }).pipe(map((res: HttpResponse<InventoryUnit[]>) => res.body));
    }

    queryLocation(requestLocation: any): Observable<Location[]> {
        const cropName = this.paramContext.cropName;
        requestLocation.programUUID = this.paramContext.programUUID;
        return this.http.post<Location[]>(SERVER_API_URL + `crops/${cropName}/locations?page=0&size=10000`, requestLocation, {
            observe: 'response'
        }).pipe(map((res: HttpResponse<Location[]>) => res.body));
    }
}
