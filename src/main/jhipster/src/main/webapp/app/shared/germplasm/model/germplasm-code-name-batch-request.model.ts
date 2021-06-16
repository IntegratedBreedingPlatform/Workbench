import { GermplasmNameSettingModel } from './germplasm-name-setting.model';

export class GermplasmCodeNameBatchRequestModel {
    constructor(
        public gids?: number[],
        public nameType?: GermplasmCodeNameType,
        public germplasmCodeNameSetting?: GermplasmNameSettingModel
    ) {
    }
}

export enum GermplasmCodeNameType {
    CODE1 = 'CODE1',
    CODE2 = 'CODE2',
    CODE3 = 'CODE3'
}
