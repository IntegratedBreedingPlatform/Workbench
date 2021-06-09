export class GermplasmGroup {
    constructor(
        public founder?: GermplasmGroupMember,
        public groupId?: number,
        public isGenerative?: boolean,
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
