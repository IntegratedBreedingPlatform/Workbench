export class TemplateModel {
    constructor(
        public templateId?: number,
        public programUUID?: string,
        public templateName?: number,
        public templateType?: string,
        public templateDetails?: TemplateDetails[]
    ) {
    }
}

export class TemplateDetails {
    constructor(
        public variableId?: number,
        public name?: string,
        public type?: string
    ) {
    }
}