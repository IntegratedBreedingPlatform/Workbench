import { Variable } from '../../ontology/model/variable';

export class FileMetadata {
    constructor(
        public fileId: number,
        public fileUUID: string,
        public name?: string,
        public description?: string,
        public path?: string,
        public url?: string,
        public copyright?: string,
        public size?: number,
        public imageHeight?: number,
        public imageWidth?: number,
        public imageLocation?: any,
        public mimeType?: string,
        public fileTimestamp?: any,
        public observationUnitId?: string,
        public ndExperimentId?: number,
        public variables?: Variable[]
    ) {
    }
}
