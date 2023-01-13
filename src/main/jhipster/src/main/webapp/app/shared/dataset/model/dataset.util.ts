import { DatasetTypeEnum } from './dataset-type.enum';

export function isObservationOrSubObservationDataset(): boolean {
    return this.datasetType === DatasetTypeEnum.PLOT ||
        this.datasetType === DatasetTypeEnum.PLANT_SUBOBSERVATIONS ||
        this.datasetType === DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS ||
        this.datasetType === DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS ||
        this.datasetType === DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS;
}
