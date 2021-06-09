import { GermplasmCodeNameSettingModel } from './germplasm-name-setting.model';

export class GermplasmCodeNameBatchRequestModel {
    constructor(
        public gids?: number[],
        public nameType?: string,
        public germplasmCodeNameSetting?: GermplasmCodeNameSettingModel
    ) {
    }
}
