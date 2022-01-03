export class Program {
    constructor(
        public id?: string,
        public uniqueID?: string,
        public name?: string,
        public createdBy?: string,
        public members?: string[],
        public crop?: string,
        public startDate?: string,
    ) {
    }
}
