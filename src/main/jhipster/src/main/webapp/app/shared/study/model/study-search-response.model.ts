export class StudySearchResponse {
    constructor(
        public studyId: number,
        public studyName: string,
        public studyTypeName: string,
        public locked: boolean,
        public ownerName: string,
        public startDate: number,
        public parentFolderName: string,
        public objective: string
    ) {
    }
}
