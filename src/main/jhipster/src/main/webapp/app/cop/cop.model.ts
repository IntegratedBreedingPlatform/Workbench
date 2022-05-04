export class CopResponse {
    constructor(
        public progress: number,
        public matrix?: any,
        public upperTriangularMatrix?: string[][],
        public hasFile?: boolean,
        /**
         * map of nameCode to map of gid to name
         */
        public germplasmCommonNamesMap?: {[key: string]: {[key: number]: string} }
    ) {
    }
}
