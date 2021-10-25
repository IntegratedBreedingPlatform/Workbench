import { ListEntry, ListModel } from '../list-builder/model/list.model';

// TODO move to shared/list-creation/model
export class SampleList extends ListModel {
    constructor(

    ) {
        super();
    }
}

export class SampleListEntry extends ListEntry {
    constructor(
        public sampleId?: number,
        public sampleNumber?: number
    ) {
        super();
    }
}
