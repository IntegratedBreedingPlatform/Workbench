import * as moment from 'moment';

export function formatDate(date: any, format: DateFormatEnum): string {
    return moment(date).format(format);
}

export function formatDateToUTC(date: any, format: DateFormatEnum): string {
    return moment.utc(date).format(format);
}

export function isValidDate(date: any, format: DateFormatEnum): boolean {
    return moment(date, format, true).isValid();
}

export enum DateFormatEnum {
    ISO_8601 = 'YYYY-MM-DD',
    ISO_8601_AND_TIME = 'YYYY-MM-DD HH:mm:ss',
    ISO_8601_NUMBER = 'YYYYMMDD'
}
