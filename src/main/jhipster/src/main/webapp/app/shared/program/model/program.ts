export class Program {
    constructor(
        public id: number,
        public programUUID: string,
        public name: string,
        public createdBy: string,
        public members: string[],
        public cropName: string,
        public startDate: string,
    ) {
    }
}
