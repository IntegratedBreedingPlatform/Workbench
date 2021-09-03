export class VariableFilterRequest {
    constructor(
        public propertyIds?: string[],
        public methodIds?: string[],
        public scaleIds?: string[],
        public variableIds?: string[],
        public exclusionVariableIds?: string[],
        public dataTypeIds?: string[],
        public variableTypeIds?: string[],
        public variableNames?: string[],
        public propertyClasses?: string[],
        public datasetIds?: number[],
        public germplasmUUIDs?: string[]
    ) {
    }
}
