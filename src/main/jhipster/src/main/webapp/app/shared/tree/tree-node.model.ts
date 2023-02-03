export class TreeNode {
    constructor(
        public key: string,
        public parentId: string,
        public name?: string,
        public owner?: string,
        public ownerUserName?: string,
        public ownerId?: number,
        public description?: string,
        public type?: string,
        public isFolder?: boolean,
        public children?: Array<TreeNode>,
        public numOfChildren?: number,
        public noOfEntries?: number,
        public isLocked?: boolean
    ) {
    }
}
