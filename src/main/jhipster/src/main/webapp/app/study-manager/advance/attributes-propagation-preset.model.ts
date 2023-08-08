export class AttributesPropagationPresetModel {
    constructor(
        public id?: number,
        public programUUID?: string,
        public toolId?: number,
        public toolSection?: string,
        public name?: string,
        public type?: string,
        public selectedDescriptorIds?: number[]
    ) {
    }
}