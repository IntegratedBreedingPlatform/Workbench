export class VariableSearchRequest {

    constructor(
        public nameFilter?: any,
        public variableIds?: number[],
        public variableTypeIds?: number[]
    ) {
    }

}
