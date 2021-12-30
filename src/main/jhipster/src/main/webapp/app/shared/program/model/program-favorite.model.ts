import { ProgramFavoriteTypeEnum } from './program-favorite-type.enum';

export class ProgramFavorite {
    constructor(
        public programFavoriteId: number,
        public entityType: ProgramFavoriteTypeEnum,
        public entityId: number,
        public programUUID: string
    ) {
    }
}
