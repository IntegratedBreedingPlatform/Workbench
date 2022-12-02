import { StudyInstanceModel } from './study-instance.model';
import { ObservationVariable } from '../../model/observation-variable.model';

export class DatasetModel {

    constructor(public datasetId: number,
                public datasetTypeId: number,
                public name: string,
                public studyId: number,
                public cropName: string,
                public instances: StudyInstanceModel[],
                public variables: ObservationVariable[] ) {
    }
}
