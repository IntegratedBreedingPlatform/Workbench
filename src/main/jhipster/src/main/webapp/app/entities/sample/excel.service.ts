import {Injectable} from '@angular/core';
import * as XLSX from 'xlsx';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ExcelService {

    constructor() {
    }

    parse(target: DataTransfer): Observable<Array<Array<any>>> {

        const reader: FileReader = new FileReader();

        const observable: Observable<Array<Array<any>>> = new Observable( (observer) => {

            reader.onload = (e: any) => {
                /* read workbook */
                const bstr: string = e.target.result;
                const wb: XLSX.WorkBook = XLSX.read(bstr, {type: 'binary'});

                /* grab first sheet */
                const wsname: string = wb.SheetNames[0];
                const ws: XLSX.WorkSheet = wb.Sheets[wsname];

                /* save data */
                const data = <Array<Array<any>>>(XLSX.utils.sheet_to_json(ws, {header: 1}));
                observer.next(data);

            };

            reader.readAsBinaryString(target.files[0]);

        });

        return observable;

    }
}
