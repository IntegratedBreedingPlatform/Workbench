import { GA4GHSearchRequest } from './ga4gh-search-request';

export class GigwaExportRequest extends GA4GHSearchRequest {
    exportFormat: string;
    exportedIndividuals: string[];
    keepExportOnServer: boolean;
};
