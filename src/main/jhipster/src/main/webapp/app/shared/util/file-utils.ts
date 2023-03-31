import { Observable } from 'rxjs/Observable';
import * as XLSX from 'xlsx';
import { HttpResponse } from '@angular/common/http';

/**
 * Parse file as a multidimensional array
 * @param file
 */
export function parseFile(file: File, sheetName): Observable<Array<Array<any>>> {
    const reader: FileReader = new FileReader();

    const observable: Observable<Array<Array<any>>> = new Observable((observer) => {

        reader.onload = (e: any) => {
            /* read workbook */
            const bstr: string = e.target.result;
            const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' });

            /* grab sheet */
            if (wb.SheetNames.includes(sheetName)) {
                const ws: XLSX.WorkSheet = wb.Sheets[sheetName];
                /* save data */
                const data = <Array<Array<any>>>(XLSX.utils.sheet_to_json(ws, { header: 1, defval: '', blankrows: false }));
                observer.next(data);
            } else {
                observer.error('Cannot find the sheet with name: ' + sheetName);
            }

        };
        reader.readAsBinaryString(file);

    });
    return observable;
}

export function exportDataJsonToExcel(fileName: string, sheetName: string, data: any): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);
    /* generate workbook and add the worksheet */
    const workbook: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, sheetName);

    /* save to file */
    XLSX.writeFile(workbook, fileName);
}

export function readAsDataURL(file: File): Promise<string> {
    return new Promise<string>((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = (event: any) => {
            resolve(event.target.result);
        }
    });
}

export function saveFile(response: HttpResponse<any>, fileName?: string) {

    if (!fileName) {
        const contentDisposition = response.headers.get('content-disposition') || '';
        const matches = /filename=([^;]+)/ig.exec(contentDisposition);
        fileName = (matches[1] || 'untitled').trim();
    }

    const url = window.URL.createObjectURL(response.body);

    // For IE 10 or later
    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveOrOpenBlob(url, fileName);
    } else { // For Chrome/Safari/Firefox and other browsers
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}

export function parseCSV(file: File): Observable<CsvFileData> {
    const reader: FileReader = new FileReader();
    const observable: Observable<CsvFileData> = new Observable((observer) => {

        reader.onload = (e: any) => {
            /* read workbook */
            const csvData = reader.result;
            const csvRecordsArray = (<string>csvData).split(/\r\n|\n/);
            if (csvRecordsArray.length) {
                const rawHeaders: string[] = csvRecordsArray[0].split(',');
                const headers: string[] = [];
                rawHeaders.forEach((rawHeader) => {
                    headers.push(rawHeader.toUpperCase().trim());
                });

                const data: any[] = [];
                if (csvRecordsArray.length > 1) {
                    for (let i = 1; i < csvRecordsArray.length; i++) {
                        // Skip empty rows
                        if (!csvRecordsArray[i].match(/^[,\s]*$/)) {
                            const rawData = csvRecordsArray[i].split(',');
                            const row = [];
                            for (let j = 0; j < headers.length; j++) {
                                row[headers[j]] = rawData[j] ? rawData[j].trim() : null;
                            }
                            data.push(row);
                        }
                    }
                }
                observer.next(new CsvFileData(headers, data));
            } else {
                observer.next(null);
            }

        };
        reader.readAsBinaryString(file);

    });
    return observable;
}

export class CsvFileData {
    constructor(public headers?: string[],
                public data?: any[]) {
    }
}
