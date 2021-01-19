import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { map } from 'rxjs/operators';
import { BreedingLocationModel } from '../model/breeding-location.model';
import { LocationType } from '../model/location-type.model';
import { LocationModel } from '../model/location.model';

@Injectable()
export class LocationService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    queryBreedingLocation(locationId): Observable<BreedingLocationModel> {
        return this.http.get<BreedingLocationModel>(SERVER_API_URL + `crops/${this.context.cropName}/locations/${locationId}`,
            { observe: 'response' }).pipe(map((res: HttpResponse<BreedingLocationModel>) => res.body));
    }

    queryLocationTypes(): Observable<LocationType[]> {
        return this.http.get<LocationType[]>(SERVER_API_URL + `crops/${this.context.cropName}/location-types`,
            { observe: 'response' }).pipe(map((res: HttpResponse<LocationType[]>) => res.body));
    }

    queryLocationsByType(locationTypes, favoriteLocation): Observable<LocationModel[]> {

        const locationSearchRequest = {
            locationTypes: locationTypes,
            favourites: favoriteLocation,
            programUUID: this.context.programUUID
        }
        return this.http.post<LocationModel[]>(SERVER_API_URL + `crops/${this.context.cropName}/locations?page=0&size=10000`, locationSearchRequest,
            { observe: 'response' }).pipe(map((res: HttpResponse<LocationModel[]>) => res.body));
    }
}
