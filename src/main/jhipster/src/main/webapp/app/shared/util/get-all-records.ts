import { Observable } from 'rxjs';
import { expand, map, reduce } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';

const GET_ALL_PAGE_SIZE = 1000;
const GET_ALL_LIMIT = 50000;

/**
 * Consume a paginated api until no more records are found
 * @param f function that does the http request (page should be 0-indexed)
 */
export function getAllRecords<T>(f: (page: number, pageSize: number) => Observable<HttpResponse<T[]>>): Observable<T[]> {
    let page = 0;
    let count;

    const mapReponse = () => {
        return map((resp: HttpResponse<T[]>) => {
            const totalCount = resp.headers.get('X-Total-Count');
            const filteredCount = resp.headers.get('X-Filtered-Count');
            count = filteredCount ? filteredCount : totalCount;
            return resp.body;
        });
    }

    return f(page, GET_ALL_PAGE_SIZE).pipe(
        mapReponse(),
        expand((resp) => {
            const nextPageRecord = (page + 1) * GET_ALL_PAGE_SIZE;
            if (resp.length && nextPageRecord < GET_ALL_LIMIT && nextPageRecord < count) {
                page++;
                return f(page, GET_ALL_PAGE_SIZE).pipe(mapReponse());
            } else {
                return [];
            }
        }),
        reduce((accumulator, resp) => accumulator.concat(resp), [])
    );
}
