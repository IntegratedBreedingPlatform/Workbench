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

    save(blob: Blob, fileName: string) {

        const url = window.URL.createObjectURL(blob);

        // For IE 10 or later
        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(url, fileName);
        } else { // For Chrome/Safari/Firefox and other browsers with HTML5 support
            const link = document.createElement('a');
            link.href = url;
            link.download = fileName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    }
}
