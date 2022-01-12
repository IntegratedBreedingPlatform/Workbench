import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { map } from 'rxjs/operators';
import { Location } from '../model/location';
import { LocationType } from '../model/location-type';
import { LocationSearchRequest } from '../model/location-search-request.model';
import { createRequestOption } from '../..';

@Injectable()
export class LocationService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getLocationById(locationId): Observable<Location> {
        return this.http.get<Location>(SERVER_API_URL + `crops/${this.context.cropName}/locations/${locationId}`,
            { observe: 'response' }).pipe(map((res: HttpResponse<Location>) => res.body));
    }

    getLocationTypes(): Observable<LocationType[]> {
        return this.http.get<LocationType[]>(SERVER_API_URL + `crops/${this.context.cropName}/location-types`,
            { observe: 'response' }).pipe(map((res: HttpResponse<LocationType[]>) => res.body));
    }

    searchLocations(request: LocationSearchRequest, favoriteLocation: boolean, pagination: any): Observable<HttpResponse<Location[]>> {
        if (favoriteLocation) {
            request.filterFavoriteProgramUUID = true;
            request.favoriteProgramUUID = this.context.programUUID;
        }

        const params = createRequestOption(pagination);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/locations/search?programUUID=${this.context.programUUID}`;
        return this.http.post<Location[]>(url, request, { params, observe: 'response' });
    }

    createLocation(locationRequest: any) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/locations?programUUID=` + this.context.programUUID;
        return this.http.post(url, locationRequest);
    }

    updateLocation(locationRequest: any, locationId: number) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/locations/${locationId}?programUUID=` + this.context.programUUID;
        return this.http.put(url, locationRequest);

    }

    deleteLocation(locationId: number) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/locations/${locationId}?programUUID=` + this.context.programUUID;
        return this.http.delete(url);
    }
}
