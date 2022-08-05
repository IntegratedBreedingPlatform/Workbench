export class SearchGermplasmRequest {
    constructor(
        public accessionNumbers?: Array<string>,
        public commonCropNames?: Array<string>,
        public germplasmDbIds?: Array<string>,
        public germplasmGenus?: Array<string>,
        public germplasmNames?: Array<string>,
        public germplasmPUIs?: Array<string>,
        public germplasmSpecies?: Array<string>,
        public parentDbIds?: Array<string>,
        public progenyDbIds?: Array<string>,
        public studyDbIds?: Array<string>,
        public synonyms?: Array<string>,
        public programDbIds?: Array<string>,
        public externalReferenceIds?: Array<string>,
        public page?: number,
        public pageSize?: number
    ) {
    }
}
