export class StudySearchRequest {
    constructor(
        public studyNameFilter?: any,
        public studyTypeIds?: number[],
        public locked?: boolean,
        public ownerName?: string,
        public studyStartDateFrom?: number,
        public studyStartDateTo?: number,
        public parentFolderName?: string,
        public objective?: string,
        public studySettings?: any
    ) {
    }
}
