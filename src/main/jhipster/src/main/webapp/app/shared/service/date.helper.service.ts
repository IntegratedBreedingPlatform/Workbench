import { Injectable } from '@angular/core';
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';

@Injectable()
export class DateHelperService {

    constructor(
        private calendar: NgbCalendar
    ) {
    }

    convertNgbDateToString(date: NgbDate) {
        return '' + date.year + this.twoDigit(date.month) + this.twoDigit(date.day);
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

    private twoDigit(n) {
        return (n < 10 ? '0' : '') + n;
    }

}
