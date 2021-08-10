import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { FileMetadata } from '../model/file-metadata';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { saveFile } from '../../util/file-utils';
import { Pageable } from '../../model/pageable';
import { createRequestOption } from '../..';

@Injectable()
export class FileService {

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    listFileMetadata(observationUnitUUID, variableName, pageable: Pageable): Observable<HttpResponse<FileMetadata[]>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const request = {
            observationUnitUUID,
        };
        if (variableName) {
            request['variableName'] = variableName;
        }
        const params: any = createRequestOption(Object.assign({
            programUUID: this.context.programUUID
        }, pageable));
        return this.http.post<FileMetadata[]>(baseUrl + '/filemetadata/search', request, { params, observe: 'response' });
    }

    upload(file: File, observationUnitUUID, termId = null): Observable<FileMetadata> {
        const formData: FormData = new FormData();
        formData.append('file', file, file.name);
        const headers = new Headers();
        headers.append('Content-Type', '');

        const options = {
            params: {
                observationUnitUUID
            },
        };

        if (termId) {
            options.params['termId'] = termId;
        }

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
