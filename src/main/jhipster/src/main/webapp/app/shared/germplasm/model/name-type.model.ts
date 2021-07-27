export class NameType {
    constructor(
        public id?: number,
        public code?: string,
        private name?: string,
        private description?: string,
    ) {
    }
}

export class NameTypeDetails {
    constructor(
        public id?: number,
        public code?: string,
        private name?: string,
        private description?: string,
        private date?: string,
        private userName?: string,
    ) {
    }
}
