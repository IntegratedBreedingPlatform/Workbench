export class GermplasmUpdateModel {

    constructor(
        public  gid: number,
        public germplasmUUID: string,
        public preferredName: string,
        public locationAbbreviation: string,
        public creationDate: string,
        public breedingMethodAbbr: string,
        public reference: string,
        public names: Map<string, string>,
        public attributes: Map<string, string>
    ) {
    }

}