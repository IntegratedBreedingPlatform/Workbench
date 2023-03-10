import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { Observable } from 'rxjs';
import { ParamContext } from '../../../shared/service/param.context';
import { SampleGenotypeImportRequest } from './sample-genotype-import-request';

@Injectable()
export class SampleGenotypeService {

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    importSampleGenotypes(genotypes: SampleGenotypeImportRequest[]): Observable<HttpResponse<number[]>> {
        return this.http.post<any>(SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/samples/genotypes`,
            genotypes, { observe: 'response' });
    }
}
