export class GermplasmGroup {
    constructor(
        public founderGid?: number,
        public groupId?: number,
        public generative?: boolean,
        public groupMembers?: GermplasmGroupMember[]
    ) {
    }
}

export class GermplasmGroupMember {
    constructor(
        public gid?: number,
        public preferredName?: string,
    ) {
    }
}
