export class GermplasmTreeNode {
    constructor(
        public gid?: number,
        public preferredName?: string,
        public femaleParentNode?: GermplasmTreeNode,
        public maleParentNode?: GermplasmTreeNode,
        public otherProgenitors?: GermplasmTreeNode[],
        public numberOfGenerations?: number
    ) {
    }
}
