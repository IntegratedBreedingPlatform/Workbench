import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { map } from 'rxjs/operators';
import { BreedingMethod } from '../model/breeding-method';
import { BreedingMethodType } from '../model/breeding-method-type.model';
import { BreedingMethodClass } from '../model/breeding-method-class.model';
import { BreedingMethodGroup } from '../model/breeding-method-group.model';
import { createRequestOption } from '../..';
import { BreedingMethodSearchRequest } from '../model/breeding-method-search-request.model';

@Injectable()
export class BreedingMethodService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    queryBreedingMethod(breedingMethodId: number): Observable<BreedingMethod> {
        return this.http.get<BreedingMethod>(SERVER_API_URL + `crops/${this.context.cropName}/breedingmethods/${breedingMethodId}`,
            { observe: 'response' }).pipe(map((res: HttpResponse<BreedingMethod>) => res.body));
    }

    queryBreedingMethodTypes(): Observable<BreedingMethodType[]> {
        return this.http.get<BreedingMethodType[]>(SERVER_API_URL + `crops/${this.context.cropName}/breedingmethod-types`,
            { observe: 'response' }).pipe(map((res: HttpResponse<BreedingMethodType[]>) => res.body));
    }

    queryBreedingMethodClasses(): Observable<BreedingMethodClass[]> {
        return this.http.get<BreedingMethodClass[]>(SERVER_API_URL + `crops/${this.context.cropName}/breedingmethod-classes`,
            { observe: 'response' }).pipe(map((res: HttpResponse<BreedingMethodClass[]>) => res.body));
    }

    queryBreedingMethodGroups(): Observable<BreedingMethodGroup[]> {
        return this.http.get<BreedingMethodGroup[]>(SERVER_API_URL + `crops/${this.context.cropName}/breedingmethod-groups`,
            { observe: 'response' }).pipe(map((res: HttpResponse<BreedingMethodGroup[]>) => res.body));
    }

    searchBreedingMethods(request: BreedingMethodSearchRequest, favoriteMethod: boolean, pagination: any): Observable<HttpResponse<BreedingMethod[]>> {
        if (favoriteMethod) {
            request.filterFavoriteProgramUUID = true;
            request.favoriteProgramUUID = this.context.programUUID;
        }

        const params = createRequestOption(pagination);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/breedingmethods/search?programUUID=${this.context.programUUID}`;
        return this.http.post<BreedingMethod[]>(url, request, { params, observe: 'response' });
    }

    createBreedingMethod(breedingMethodRequest: any) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/breedingmethods?programUUID=` + this.context.programUUID;
        return this.http.post(url, breedingMethodRequest);
    }

    updateBreedingMethod(breedingMethodRequest: any, breedingMethodDbId: number) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/breedingmethods/${breedingMethodDbId}?programUUID=` + this.context.programUUID;
        return this.http.put(url, breedingMethodRequest);

    }

    deleteBreedingMethod(breedingMethodDbId: number) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/breedingmethods/${breedingMethodDbId}?programUUID=` + this.context.programUUID;
        return this.http.delete(url);
    }
}
