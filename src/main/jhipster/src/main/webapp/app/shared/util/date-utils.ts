import * as moment from 'moment';

export const DATE_FORMAT: String = 'YYYY-MM-DD HH:mm:ss';

export function parseDate(date: number): string {
    return moment(date).format(DATE_FORMAT);
}
