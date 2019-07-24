export class Permission {
    constructor(
        public id?: string,
        public name?: string,
        public description?: string,
        public children?: Permission[],
        public selectable?: boolean
    ) {
    }
}
