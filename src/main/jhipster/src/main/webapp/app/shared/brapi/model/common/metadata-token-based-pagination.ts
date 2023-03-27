import { MetadataDatafiles } from './metadata-datafiles';
import { MetadataStatus } from './metadata-status';
import { MetadataPaginationTokenBased } from './metadata-pagination-token-based';

export interface MetadataTokenBasedPagination {
    datafiles?: Array<MetadataDatafiles>;
    pagination?: MetadataPaginationTokenBased;
    status?: Array<MetadataStatus>;
}
