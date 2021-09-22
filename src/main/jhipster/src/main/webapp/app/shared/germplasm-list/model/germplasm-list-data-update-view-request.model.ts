import { GermplasmListColumnCategory } from './germplasm-list-column-category.type';

export class GermplasmListDataUpdateViewRequest {
    constructor(public id: number,
                public category: GermplasmListColumnCategory,
                public typeId?: number) {
    }
}
