import { DateFormatEnum, formatDate, isValidDate } from '../../../../../../main/webapp/app/shared/util/date-utils';

describe('date-utils', () => {
    it('should format dates', () => {
        expect(formatDate('20200515', DateFormatEnum.ISO_8601)).toBe('2020-05-15')
        expect(formatDate('20200515', DateFormatEnum.ISO_8601_AND_TIME)).toBe('2020-05-15 00:00:00')
        expect(formatDate('2020-05-15 10:00:00', DateFormatEnum.ISO_8601_AND_TIME)).toBe('2020-05-15 10:00:00')
        expect(formatDate('2020-05-15 10:00:00', DateFormatEnum.ISO_8601)).toBe('2020-05-15')
        expect(formatDate('2020-05-15 10:00:00', DateFormatEnum.ISO_8601_NUMBER)).toBe('20200515')
        // unix epoch
        expect(formatDate(1624995973789, DateFormatEnum.ISO_8601_AND_TIME)).toBe('2021-06-29 16:46:13')
    })

    it('should validate dates', () => {
        expect(isValidDate('20200515', DateFormatEnum.ISO_8601)).toBe(false)
        expect(isValidDate('20200515', DateFormatEnum.ISO_8601_NUMBER)).toBe(true)
        expect(isValidDate('2020-05-15 10:00:00', DateFormatEnum.ISO_8601)).toBe(false)
        expect(isValidDate('2020-05-15 10:00:00', DateFormatEnum.ISO_8601_AND_TIME)).toBe(true)
        expect(isValidDate('2020-05-15', DateFormatEnum.ISO_8601)).toBe(true)
        expect(isValidDate('2020-05-15', DateFormatEnum.ISO_8601_NUMBER)).toBe(false)
    })

});
