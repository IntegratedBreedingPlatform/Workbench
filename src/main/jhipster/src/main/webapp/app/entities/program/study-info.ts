export class StudyInfo {
    constructor(
        public name?: string,
        public type?: string,
        public date?: string,
        public folder?: string,
        public selected?: boolean,
        public metadata?: StudyMetadata
    ) {
    }
}

export class StudyMetadata {
    constructor(
        public observations?: any
    ) {
    }
}
