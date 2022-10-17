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

export class NameTypeMetaData {
    constructor(
        public studies?: number,
        public germplasm?: number,
        public germplasmList?: number
    ) {
    }
}
