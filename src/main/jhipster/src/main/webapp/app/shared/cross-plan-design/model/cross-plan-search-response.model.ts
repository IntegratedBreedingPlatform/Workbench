export class CrossPlanSearchResponse {
    constructor(
        public id: number,
        public name: string,
        public parentFolderName: string,
        public description: string,
        public createdBy: string,
        public type: string,
        public creationDate: string,
        public notes?: string
    ){}
}
