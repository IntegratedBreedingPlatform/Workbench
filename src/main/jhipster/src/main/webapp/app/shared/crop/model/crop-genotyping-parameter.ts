export class CropGenotypingParameter {
    constructor(
        public endpoint: string,
        public tokenEndpoint: string,
        public userName: string,
        public password: string,
        public programId: string,
        public baseUrl: string,
        public accessToken?: string
    ) {
    }
}
