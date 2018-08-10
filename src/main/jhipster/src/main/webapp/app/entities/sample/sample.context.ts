import {Injectable} from '@angular/core';
import {SampleList} from './sample-list.model';

@Injectable()
export class SampleContext {

    activeSampleList: SampleList;

    getActiveList(): SampleList {
        return this.activeSampleList;
    }

    setActiveList(sampleList: SampleList) {
        this.activeSampleList = sampleList;
    }

}
