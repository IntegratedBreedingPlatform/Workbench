import { Observable } from 'rxjs';
import { expand, reduce } from 'rxjs/operators';

const GET_ALL_PAGE_SIZE = 1000;
const GET_ALL_LIMIT = 50000;

/**
 * Consume a paginated api until no more records are found
 * @param f function that does the http request
 */
export function getAllRecords<T>(f: (page: number, pageSize: number) => Observable<T[]>): Observable<T[]> {
    let page = 0;
    return f(page, GET_ALL_PAGE_SIZE).pipe(
        expand((resp) => {
            if (resp.length && ((page + 1) * GET_ALL_PAGE_SIZE) <= GET_ALL_LIMIT) {
                page++;
                return f(page, GET_ALL_PAGE_SIZE);
            } else {
                return [];
            }
        }),
        reduce((accumulator, resp) => accumulator.concat(resp), [])
    );
}
