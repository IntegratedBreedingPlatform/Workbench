import { StudyInstanceModel } from './study-instance.model';
import { ObservationVariable } from '../../model/observation-variable.model';
import { DatasetTypeEnum } from './dataset-type.enum';

export class DatasetModel {

    constructor(public datasetId: number,
                public datasetTypeId: DatasetTypeEnum,
                public name: string,
                public studyId: number,
                public cropName: string,
                public instances: StudyInstanceModel[],
                public variables: ObservationVariable[] ) {
    }
}
