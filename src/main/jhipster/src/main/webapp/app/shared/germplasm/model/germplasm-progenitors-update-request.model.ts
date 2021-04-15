export class GermplasmProgenitorsUpdateRequestModel {
    constructor(
        public breedingMethodId?: number,
        public gpid1?: string,
        public gpid2?: string,
        public otherProgenitors?: number[]
    ) {
    }
}
