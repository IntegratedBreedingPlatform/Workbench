export class BreedingMethodType {
    constructor(
        public code?: string,
        public name?: string
    ) {
    }
}

export enum BreedingMethodTypeEnum {
    GENERATIVE = 'GEN',
    DERIVATIVE = 'DER',
    MAINTENANCE = 'MAN'
}
