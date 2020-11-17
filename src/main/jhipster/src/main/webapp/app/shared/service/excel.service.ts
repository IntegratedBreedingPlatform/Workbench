import {Injectable} from '@angular/core';
import * as XLSX from 'xlsx';
import {Observable} from 'rxjs';

@Injectable()
export class ExcelService {

    constructor() {
    }

    parse(target: DataTransfer, wsname: string): Observable<Array<Array<any>>> {

        const reader: FileReader = new FileReader();

        const observable: Observable<Array<Array<any>>> = new Observable( (observer) => {

            reader.onload = (e: any) => {
                /* read workbook */
                const bstr: string = e.target.result;
                const wb: XLSX.WorkBook = XLSX.read(bstr, {type: 'binary'});
                const ws: XLSX.WorkSheet = wb.Sheets[wsname];

                if (ws) {
                    const data = <Array<Array<any>>>(XLSX.utils.sheet_to_json(ws, {header: 1, defval: ''}));
                    observer.next(data);
                } else {
                    observer.error('Cannot find the specified worksheet ' + wsname);
                }

            };

            reader.readAsBinaryString(target.files[0]);

        });

        return observable;

    }
}
