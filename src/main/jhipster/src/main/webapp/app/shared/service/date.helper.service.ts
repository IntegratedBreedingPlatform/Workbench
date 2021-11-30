import { Injectable } from '@angular/core';
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';

@Injectable()
export class DateHelperService {

    public readonly YYYY_MM_DD_DASH_FORMAT = 'yyyy-mm-dd';

    constructor(
        private calendar: NgbCalendar
    ) {
    }

    convertNgbDateToString(date: NgbDate) {
        return '' + date.year + this.twoDigit(date.month) + this.twoDigit(date.day);
    }

    convertFormattedDateStringToNgbDate(dateString: string, format: string): NgbDate {
        // Convert date strings in the format to yyyy-mm-dd
        if (dateString && format && dateString.length === format.length) {
            if (format === this.YYYY_MM_DD_DASH_FORMAT) {
                const year = Number(dateString.substring(0, 4));
                const month = Number(dateString.substring(5, 7));
                const day = Number(dateString.substring(8, 10));
                return new NgbDate(year, month, day);
            }
        }
        return this.calendar.getToday();
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
