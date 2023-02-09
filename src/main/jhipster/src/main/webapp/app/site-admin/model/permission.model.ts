export class Permission {
    constructor(
        public id?: string,
        public name?: string,
        public description?: string,
        public children?: Permission[],
        public parent?: Permission,
        public selectable?: boolean,
        public selected?: boolean,
        public disabled?: boolean
    ) {
    }
}
