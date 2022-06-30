import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { FileMetadata } from '../model/file-metadata';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { Pageable } from '../../model/pageable';
import { createRequestOption } from '../..';

@Injectable()
export class FileService {

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
    }

    listFileMetadata(observationUnitUUID, germplasmUUID, variableName, instanceId, lotId, pageable: Pageable): Observable<HttpResponse<FileMetadata[]>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const request = {}
        if (observationUnitUUID) {
            request['observationUnitUUID'] = observationUnitUUID;
        }
        if (germplasmUUID) {
            request['germplasmUUID'] = germplasmUUID;
        }
        if (variableName) {
            request['variableName'] = variableName;
        }

        if (instanceId) {
            request['instanceIds'] = [instanceId];
        }

        if (lotId) {
            request['lotId'] = lotId;
        }
        const params: any = createRequestOption(Object.assign({
            programUUID: this.context.programUUID
        }, pageable));
        return this.http.post<FileMetadata[]>(baseUrl + '/filemetadata/search', request, { params, observe: 'response' });
    }

    upload(file: File, observationUnitUUID, germplasmUUID, termId = null, instanceId, lotId): Observable<FileMetadata> {
        const formData: FormData = new FormData();
        formData.append('file', file, file.name);
        const headers = new Headers();
        headers.append('Content-Type', '');

        const params = {};
        if (observationUnitUUID) {
            params['observationUnitUUID'] = observationUnitUUID;
        }
        if (germplasmUUID) {
            params['germplasmUUID'] = germplasmUUID;
        }
        if (termId) {
            params['termId'] = termId;
        }
        if (instanceId) {
            params['instanceId'] = instanceId
        }
        if (lotId) {
            params['lotId'] = lotId
        }
        const options = {params};

        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.post<FileMetadata>(baseUrl + '/files?programUUID=' + this.context.programUUID, formData, options);
    }

    downloadFile(path): Observable<HttpResponse<Blob>> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.get(baseUrl + '/files/' + path, { observe: 'response', responseType: 'blob' });
    }

    delete(fileUUID) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.delete(baseUrl + '/files/' + fileUUID + '?programUUID=' + this.context.programUUID);
    }

    getFileCount(variableIds, germplasmUUID, lotId) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        if (germplasmUUID) {
            params['germplasmUUID'] = germplasmUUID;
        }
        if (lotId) {
            params['lotId'] = lotId
        }
        return this.http.head(baseUrl + '/filemetadata', { params, observe: 'response'});
    }

    detachFiles(variableIds, germplasmUUID, lotId) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        if (germplasmUUID) {
            params['germplasmUUID'] = germplasmUUID;
        }
        if (lotId) {
            params['lotId'] = lotId
        }
        return this.http.delete(baseUrl + '/filemetadata/variables', {params});
    }

    removeFiles(variableIds, germplasmUUID, lotId) {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        const params = {};
        if (germplasmUUID) {
            params['germplasmUUID'] = germplasmUUID;
        }
        if (lotId) {
            params['lotId'] = lotId
        }
        return this.http.delete(baseUrl + '/filemetadata', {params});
    }

   isFileStorageConfigured(): Promise<boolean> {
        const baseUrl = SERVER_API_URL + 'crops/' + this.context.cropName;
        return this.http.get(baseUrl + '/filestorage/status').toPromise().then((resp: any) => resp.status);
    }

}
