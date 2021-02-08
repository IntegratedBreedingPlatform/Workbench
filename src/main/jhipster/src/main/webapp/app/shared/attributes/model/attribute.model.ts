// TODO
//  - move to shared/germplasm
//  - rename to AttributeType
export class Attribute {
    constructor(
        public  code?: string,
        private id?: number,
        private name?: string,
    ) {
    }
}
