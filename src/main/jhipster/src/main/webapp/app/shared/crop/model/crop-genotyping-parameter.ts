export class CropGenotypingParameter {
    constructor(
        public cropName: string,
        public endpoint: string,
        public tokenEndpoint: string,
        public userName: string,
        public password: string,
        public programId: string,
        public baseUrl: string
    ) {
    }
}
