export class UserSearchRequest {
    constructor(public status?: number,
                public userName?: string,
                public firstName?: string,
                public lastName?: string,
                public email?: string,
                public roleIds?: number[],
                public crops?: string[]) {
    }
}
