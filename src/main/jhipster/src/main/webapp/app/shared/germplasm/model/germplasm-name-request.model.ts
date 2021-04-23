export class GermplasmNameRequestModel {
    constructor(
        public name?: string,
        public date?: string,
        public locationId?: number,
        public nameTypeCode?: string,
        public preferredName?: boolean,
    ) {
    }
}
