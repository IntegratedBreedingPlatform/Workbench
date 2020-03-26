import { BaseEntity } from './../../../shared';

export class TreeNode implements BaseEntity {
    constructor(
        public id: string,
        public key: string,
        public parentId: string,
        public name?: string,
        public title?: string,
        public owner?: string,
        public description?: string,
        public type?: string,
        public isFolder?: boolean,
        public children?: Array<TreeNode>,
        public numOfChildren?: number,
        public noOfEntries?: number
    ) {
    }
}
