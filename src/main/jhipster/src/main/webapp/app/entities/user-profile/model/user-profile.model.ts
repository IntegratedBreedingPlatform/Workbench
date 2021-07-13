export class UserProfileModel {
    constructor(
        public password?: string,
        public firstName?: string,
        private lastName?: string,
        private email?: string,

    ) {
    }
}
