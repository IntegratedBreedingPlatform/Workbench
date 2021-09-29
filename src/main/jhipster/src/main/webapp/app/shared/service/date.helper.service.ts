import { Injectable } from '@angular/core';
import { NgbCalendar, NgbDate, NgbDateParserFormatter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

@Injectable()
export class DateHelperService {

    constructor(
        private calendar: NgbCalendar,
        private ngbDateParserFormatter: NgbDateParserFormatter
    ) {
    }

    convertNgbDateToString(date: NgbDate) {
        return '' + date.year + this.twoDigit(date.month) + this.twoDigit(date.day);
    }

    convertNgbDateToStringIso(date: NgbDateStruct) {
        return this.ngbDateParserFormatter.format(date)
    }

    convertStringToNgbDate(dateString: string): NgbDate {
        if (dateString && dateString.length === 8) {
            const year = Number(dateString.substring(0, 4));
            const month = Number(dateString.substring(4, 6));
            const day = Number(dateString.substring(6, 8));
            return new NgbDate(year, month, day);
        }
        return this.calendar.getToday();
    }

    convertStringIsoToNgbDate(dateString: string): NgbDateStruct {
        return this.ngbDateParserFormatter.parse(dateString);
    }

    private twoDigit(n) {
        return (n < 10 ? '0' : '') + n;
    }

}
