export class BrapiResponse<T> {
    constructor(
        public metadata?: Pagination,
        public result?: BrapiResult<T>
    ) {
    }
}

export class Pagination {
    constructor(
        public pageSize?: number,
        public totalCount?: number,
        public totalPages?: number,
        public currentPage?: number
    ) {
    }
}

export class BrapiResult<T> {
    constructor(
        public data?: T[],
    ) {
    }
}
