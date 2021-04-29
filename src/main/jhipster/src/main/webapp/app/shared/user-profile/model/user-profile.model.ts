export class UserProfileModel {
    constructor(
        public userName?: string,
        public password?: string,
        public firstName?: string,
        private lastName?: string,
        private email?: string,

    ) {
    }
}
