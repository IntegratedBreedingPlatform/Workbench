import { ProgramFavorite } from '../../program/model/program-favorite.model';

export class BreedingMethod {
    constructor(
        public code?: string,
        public name?: string,
        public description?: string,
        public type?: string,
        public group?: string,
        public methodClass?: number,
        public methodClassName?: string,
        public mid?: number,
        public numberOfProgenitors?: number,
        public creationDate?: string,
        public separator?: string,
        public prefix?: string,
        public count?: string,
        public suffix?: string,
        public programFavorites?: ProgramFavorite[],
        public snameTypeCode?: string
    ) {
    }
}
