import { ListResponse } from './list-response';
import { Metadata } from './metadata';
import { ListResponseResult } from './list-response-result';

export class BrapiListResponse<T> implements ListResponse<T> {
    metadata: Metadata;
    result: ListResponseResult<T>;

    constructor(metadata: Metadata,
                result: ListResponseResult<T>) {
        this.metadata = metadata;
        this.result = result;
    }
}
