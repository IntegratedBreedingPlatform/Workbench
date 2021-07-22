import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FileMetadata } from '../model/file-metadata';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';

@Injectable()
export class FileService {

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    listFileMetadata(observationId): Observable<FileMetadata[]> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params: any = { observationId }
        return this.http.get<FileMetadata[]>(baseUrl + '/filemetadata', {params, observe: 'body'});
    }

}
