export class UserProfileModel {
    constructor(
        private userId?: number,
        public firstName?: string,
        private lastName?: string,
        private email?: string,
        private restPastEmail?: boolean,

    ) {
    }
}
