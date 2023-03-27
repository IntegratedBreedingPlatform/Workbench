import { ListResponseResult } from './list-response-result';
import { ListResponseTokenBasedPagination } from './list-response-token-based-pagination';
import { MetadataTokenBasedPagination } from './metadata-token-based-pagination';

export class BrapiListResponseTokenBasedPagination<T> implements ListResponseTokenBasedPagination<T> {
    metadata: MetadataTokenBasedPagination;
    result: ListResponseResult<T>;

    constructor(metadata: MetadataTokenBasedPagination,
                result: ListResponseResult<T>) {
        this.metadata = metadata;
        this.result = result;
    }
}
