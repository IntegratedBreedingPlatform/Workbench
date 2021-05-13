export class GermplasmNeighborhoodTreeNode {
    constructor(
        public gid?: number,
        public preferredName?: string,
        public linkedNodes?: GermplasmNeighborhoodTreeNode[]
    ) {
    }
}
