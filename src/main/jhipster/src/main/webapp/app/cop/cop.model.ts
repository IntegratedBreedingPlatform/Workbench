export class CopResponse {
    constructor(
        public progress: number,
        public matrix?: any,
        public upperTriangularMatrix?: string[][],
        public hasFile?: boolean
    ) {
    }
}
