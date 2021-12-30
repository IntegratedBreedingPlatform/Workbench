import { ProgramFavoriteTypeEnum } from './program-favorite-type.enum';

export class ProgramFavoriteAddRequest {
    constructor(public favoriteType: ProgramFavoriteTypeEnum,
                public entityIds: Array<number>) {
    }
}
