/**
 * Search or apply actions over either a filter or some items
 */
export class SearchComposite<X, Y> {
    constructor(
        public searchRequest?: X,
        public itemIds?: Y[]
    ) {

    }
}
