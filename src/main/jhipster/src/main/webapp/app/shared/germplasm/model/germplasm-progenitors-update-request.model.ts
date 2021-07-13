export class GermplasmProgenitorsUpdateRequestModel {
    constructor(
        public breedingMethodId?: number,
        public gpid1?: number,
        public gpid2?: number,
        public otherProgenitors?: number[]
    ) {
    }
}
