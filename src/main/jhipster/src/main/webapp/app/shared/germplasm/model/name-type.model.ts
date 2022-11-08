export class NameType {
    constructor(
        public id?: number,
        public code?: string,
        public name?: string,
        public description?: string,
    ) {
    }
}

export class NameTypeDetails {
    constructor(
        public id?: number,
        public code?: string,
        public name?: string,
        public description?: string,
        public date?: string,
        public userName?: string,
    ) {
    }
}

export class NameTypeMetadata {
    constructor(
        public studiesCount?: number,
        public germplasmCount?: number,
        public germplasmListCount?: number
    ) {
    }
}
