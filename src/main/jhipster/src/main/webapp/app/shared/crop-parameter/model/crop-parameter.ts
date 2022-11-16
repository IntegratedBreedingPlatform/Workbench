export class CropParameter {
    constructor(
        public key: string,
        public value: string,
        public encrypted: boolean,
        public description?: string
    ) {
    }
}
