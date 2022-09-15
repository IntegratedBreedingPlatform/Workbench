export class ExportFlapjackRequest {
    constructor(
        public exportFormat: string,
        public exportedIndividuals: string[],
        public keepExportOnServer: boolean, // Must be true, so that we can load the files from the server in flapjack-bytes
        public pageSize: number) {
    }
};
