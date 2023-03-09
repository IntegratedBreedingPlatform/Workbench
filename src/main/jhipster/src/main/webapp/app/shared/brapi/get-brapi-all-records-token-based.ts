import { Observable } from 'rxjs';
import { expand, map, reduce } from 'rxjs/operators';
import { ListResponseResult } from './model/common/list-response-result';
import { ListResponseTokenBasedPagination } from './model/common/list-response-token-based-pagination';

const GET_ALL_PAGE_SIZE = 10000;
const GET_ALL_LIMIT = 50000;

/**
 * Consume a paginated api until no more records are found
 * @param f function that does the http request (page should be 0-indexed)
 */
export function getBrapiAllRecordsTokenBased<T>(f: (pageToken: number, pageSize: number) => Observable<ListResponseTokenBasedPagination<any>>):
    Observable<Array<T>> {
    let pageToken = 0;
    let recordsCount = 0;

    const mapReponse = () => {
        return map((resp: ListResponseTokenBasedPagination<ListResponseResult<any>>) => {
            if (resp.result.data) {
                recordsCount = recordsCount + resp.result.data.length;
                pageToken = resp.metadata.pagination.nextPageToken;
                return resp.result.data;
            } else {
                return new Array<any>();
            }
        });
    };

    return f(pageToken, GET_ALL_PAGE_SIZE).pipe(
        mapReponse(),
        expand((resp) => {
            if (pageToken && resp.length && recordsCount < GET_ALL_LIMIT) {
                return f(pageToken, GET_ALL_PAGE_SIZE).pipe(mapReponse());
            } else {
                return new Array<any>();
            }
        }),
        reduce((accumulator, resp) => accumulator.concat(resp), [])
    );
}
