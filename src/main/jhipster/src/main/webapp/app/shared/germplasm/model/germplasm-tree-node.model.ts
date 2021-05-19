export class GermplasmTreeNode {
    constructor(
        public gid?: number,
        public preferredName?: string,
        public methodName?: string,
        public methodCode?: string,
        public femaleParentNode?: GermplasmTreeNode,
        public maleParentNode?: GermplasmTreeNode,
        public otherProgenitors?: GermplasmTreeNode[],
        public numberOfGenerations?: number,
        public numberOfProgenitors?: number
    ) {
    }
}
