export class Variant {
    constructor(
        public additionalInfo: any,
        public alternateBases: Array<string>,
        public ciend: Array<number>,
        public cipos: Array<string>,
        public created: string,
        public end: number,
        public filtersApplied: boolean,
        public filtersFailed: Array<string>,
        public filtersPassed: boolean,
        public referenceBases: string,
        public referenceName: string,
        public start: number,
        public svlen: number,
        public updated: string,
        public variantDbId: string,
        public variantNames: Array<string>,
        public variantSetDbId: Array<string>,
        public variantType: string) {
    }
}
