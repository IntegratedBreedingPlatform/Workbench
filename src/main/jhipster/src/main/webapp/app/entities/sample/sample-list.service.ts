import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SERVER_API_URL} from '../../app.constants';
import {SampleList} from './sample-list.model';

@Injectable()
export class SampleListService {

    private resourceUrl;

    constructor(
        private http: HttpClient
    ) { }

    setCrop(crop: string) {
        this.resourceUrl =  SERVER_API_URL + `sampleLists/${crop}`;
    }

    search(searchString: string, exactMatch: boolean) {
        const result: SampleList[] = [];
        return result;
    }

}
