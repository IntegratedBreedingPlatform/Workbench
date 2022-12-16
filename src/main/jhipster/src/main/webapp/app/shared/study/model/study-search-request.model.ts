export class StudySearchRequest {
    constructor(
        public studyNameFilter?: any,
        public studyTypeIds?: number[],
        public locked?: boolean,
        public ownerName?: string,
        public studyStartDateFrom?: number,
        public studyStartDateTo?: string,
        public parentFolderName?: string,
        public objective?: string
    ) {
    }
}
