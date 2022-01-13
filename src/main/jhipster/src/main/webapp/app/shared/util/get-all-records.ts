import { Observable } from 'rxjs';
import { expand, map, reduce } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';

const GET_ALL_PAGE_SIZE = 1000;
const GET_ALL_LIMIT = 50000;

/**
 * Consume a paginated api until no more records are found
 * @param f function that does the http request
 */
export function getAllRecords<T>(f: (page: number, pageSize: number) => Observable<HttpResponse<T[]>>): Observable<T[]> {
    let page = 0;
    let totalCount;
    return f(page, GET_ALL_PAGE_SIZE).pipe(
        map((resp: HttpResponse<T[]>) => {
            totalCount = resp['X-Total-Count'];
            return resp.body;
        }),
        expand((resp) => {
            const nextPageRecord = (page + 1) * GET_ALL_PAGE_SIZE;
            if (resp.length && nextPageRecord <= GET_ALL_LIMIT && nextPageRecord <= totalCount) {
                page++;
                return f(page, GET_ALL_PAGE_SIZE);
            } else {
                return [];
            }
        }),
        reduce((accumulator, resp) => accumulator.concat(resp), [])
    );
}
