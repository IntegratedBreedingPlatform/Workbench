import { Context } from './context';
import { ListResponseResult } from './list-response-result';
import { MetadataTokenBasedPagination } from './metadata-token-based-pagination';

export interface ListResponseTokenBasedPagination<T> {
    context?: Context;
    metadata: MetadataTokenBasedPagination;
    result: ListResponseResult<T>;
}
