import { Usage } from './usage';

export class MetadataDetails {
    constructor(
        public editableFields?: string[],
        public deletable?: boolean,
        public editable?: boolean,
        public usage?: Usage
    ) {
    }
}
