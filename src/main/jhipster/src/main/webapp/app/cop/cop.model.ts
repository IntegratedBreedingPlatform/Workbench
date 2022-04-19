export class CopResponse {
    constructor(
        public progress: number,
        public matrix?: any,
        public upperTriangularMatrix?: string[][],
        public hasFile?: boolean
    ) {
    }
}

// values represent enum id
export enum BTypeEnum {
    CROSS_FERTILIZING = 0,
    SELF_FERTILIZING = 1,
    SELF_FERTILIZING_F4 = 2
}
