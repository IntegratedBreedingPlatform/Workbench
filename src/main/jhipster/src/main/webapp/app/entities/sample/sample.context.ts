import {Injectable} from '@angular/core';
import {SampleList} from './sample-list.model';

@Injectable()
export class SampleContext {
    activeList: SampleList;
}
