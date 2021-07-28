import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { FileMetadata } from '../model/file-metadata';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { saveFile } from '../../util/file-utils';

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
        return this.http.get<FileMetadata[]>(baseUrl + '/filemetadata', { params, observe: 'body' });
    }

    upload(file: File, observationUnitUUID, termId): Observable<FileMetadata> {
        const formData: FormData = new FormData();
        formData.append('file', file, file.name);
        const headers = new Headers();
        headers.append('Content-Type', '');

        const options = {
            params: {
                observationUnitUUID,
                termId
            },
        };

        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.post<FileMetadata>(baseUrl + '/files', formData, options);
    }

    downloadFile(path): Observable<HttpResponse<Blob>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.get(baseUrl + '/files/' + path, { observe: 'response', responseType: 'blob' });
    }

    delete(fileUUID) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.delete(baseUrl + '/files/' + fileUUID);
    }

}
