import { GermplasmNameSettingModel } from './germplasm-name-setting.model';

export class GermplasmNameBatchRequestModel {
    constructor(
        public gids?: number[],
        public nameType?: string,
        public germplasmNameSetting?: GermplasmNameSettingModel
    ) {
    }
}
