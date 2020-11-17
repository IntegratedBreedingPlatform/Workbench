import {Injectable} from '@angular/core';
import * as XLSX from 'xlsx';
import {Observable} from 'rxjs';

@Injectable()
export class ExcelService {

    constructor() {
    }

    parse(target: DataTransfer, wsname?: string): Observable<Array<Array<any>>> {

        const reader: FileReader = new FileReader();

        const observable: Observable<Array<Array<any>>> = new Observable( (observer) => {

            reader.onload = (e: any) => {
                /* read workbook */
                const bstr: string = e.target.result;
                const wb: XLSX.WorkBook = XLSX.read(bstr, {type: 'binary'});
                let ws: XLSX.WorkSheet;

                if (wsname) {
                    // Get the specified worksheet if specified.
                    ws = wb.Sheets[wsname];
                } else {
                    // Else just parse the first sheet.
                    ws = wb.Sheets[wb.SheetNames[0]];
                }

                if (ws) {
                    const data = <Array<Array<any>>>(XLSX.utils.sheet_to_json(ws, {header: 1, defval: '', blankrows: false}));
                    observer.next(data);
                } else {
                    observer.error('Cannot find the specified worksheet: ' + wsname);
                }

            };

            reader.readAsBinaryString(target.files[0]);

        });

        return observable;

    }
}
