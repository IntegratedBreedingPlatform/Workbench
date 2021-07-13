export class Pageable {
    constructor(
        public page?: number,
        public size?: number,
        public sort?: string[]
    ) {
    }
}
