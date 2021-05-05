export class ReleaseNote {
    constructor(
        public id: number,
        public version: string,
        public releaseDate: string,
        public hasComingSoon: boolean,
        public fileName: string
    ) {
    }
}
