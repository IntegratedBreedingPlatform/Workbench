import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import {SERVER_API_URL} from "../../../app.constants";
import {Observable} from 'rxjs';
import {ParamContext} from '../../../shared/service/param.context';
import {GenotypeImportRequest} from "./genotype.import.request";

@Injectable()
export class GenotypeService {

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }
    importGenotypes(genotypes: GenotypeImportRequest[], listId): Observable<HttpResponse<number[]>> {
        return this.http.post<any>(SERVER_API_URL + `crops/${this.context.cropName}/genotypes/${listId}?programUUID=` + this.context.programUUID,
            genotypes, { observe: 'response' });
    }
}