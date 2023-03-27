import { Observable } from 'rxjs';
import { expand, map, reduce } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { BrapiListResponse } from './model/common/brapi-list-response';
import { ListResponse } from './model/common/list-response';
import { ListResponseResult } from './model/common/list-response-result';

const GET_ALL_PAGE_SIZE = 5000;
const GET_ALL_LIMIT = 50000;

/**
 * Consume a paginated api until no more records are found
 * @param f function that does the http request (page should be 0-indexed)
 */
export function getBrapiAllRecords<T>(f: (page: number, pageSize: number) => Observable<ListResponse<any>>):
    Observable<Array<T>> {
    let page = 0;
    let totalCount = 0;

    const mapReponse = () => {
        return map((resp: ListResponse<ListResponseResult<any>>) => {
            if (resp.metadata.pagination.totalCount) {
                totalCount = resp.metadata.pagination.totalCount;
            }
            return resp.result.data ? resp.result.data : new Array<any>();
        });
    };

    return f(page, GET_ALL_PAGE_SIZE).pipe(
        mapReponse(),
        expand((resp) => {
            const nextPageRecord = (page + 1) * GET_ALL_PAGE_SIZE;
            if (resp.length && nextPageRecord < GET_ALL_LIMIT && nextPageRecord < totalCount) {
                page++;
                return f(page, GET_ALL_PAGE_SIZE).pipe(mapReponse());
            } else {
                return new Array<any>();
            }
        }),
        reduce((accumulator, resp) => accumulator.concat(resp), [])
    );
}
