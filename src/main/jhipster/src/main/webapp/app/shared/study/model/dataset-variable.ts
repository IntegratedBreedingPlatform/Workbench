export class DatasetVariable {
    constructor(
        public variableTypeId: number,
        public variableId: number,
        public studyAlias?: string
    ) {
    }
}
