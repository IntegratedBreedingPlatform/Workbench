export class GermplasmCodeNameSettingModel {
    constructor(
        public prefix?: string,
        public suffix?: string,
        public addSpaceBetweenPrefixAndCode?: boolean,
        public addSpaceBetweenSuffixAndCode?: boolean,
        public numOfDigits?: number,
        public startNumber?: number
    ) {
    }
}
