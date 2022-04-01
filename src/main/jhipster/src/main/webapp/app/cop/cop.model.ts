export class CopResponse {
    constructor(
        public progress: number,
        public matrix?: any,
        public upperTriangularMatrix?: string[][],
        public hasFile?: boolean
    ) {
    }
}

export enum BTypeEnum {
    CROSS_FERTILIZING,
    SELF_FERTILIZING,
    SELF_FERTILIZING_F4
}
