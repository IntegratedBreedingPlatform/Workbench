export class Call {
    constructor(
        public additionalInfo: any,
        public callSetDbId: string,
        public callSetName: string,
        public genotype: Genotype,
        public genotype_likelihood: number[],
        public phaseSet: string,
        public variantDbId: string,
        public variantName: string) {
    }
}

export class Genotype {
    constructor(
        public values: string[]
    ) {
    }

}
