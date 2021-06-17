export class GermplasmGroupingRequestModel {
    constructor(
        public gids?: number[],
        public includeDescendants?: boolean,
        public preserveExistingGroup?: boolean,
    ) {
    }
}
