export class CrossPlanSearchRequest {
    constructor(
        public crossPlanIds?: number[],
        public crossPlanNameFilter?: any,
        public ownerName?: string,
        public notes?: string,
        public description?: string,
        public crossPlanStartDateFrom?: number,
        public crossPlanStartDateTo?: number,
        public parentFolderName?: string,
    ) {}
}
