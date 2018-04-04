import { BaseEntity } from './../../../shared';

export class TreeNode implements BaseEntity {
    constructor(
        public id: string,
        public parentId: string,
        public name?: string,
        public owner?: string,
        public description?: string,
        public type?: string,
        public isFolder?: string,
        public children?: Array<TreeNode>,
        public numOfChildren?: number,
        public numOfEntries?: number
    ) {
    }
}
