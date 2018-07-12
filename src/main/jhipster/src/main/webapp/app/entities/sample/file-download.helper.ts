import {HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';

@Injectable()
export class FileDownloadHelper {

    constructor() {
    }

    getFileNameFromResponseContentDisposition(response: HttpResponse<Blob>) {
        const contentDisposition = response.headers.get('content-disposition') || '';
        const matches = /filename=([^;]+)/ig.exec(contentDisposition);
        const fileName = (matches[1] || 'untitled').trim();
        return fileName;
    }
}
