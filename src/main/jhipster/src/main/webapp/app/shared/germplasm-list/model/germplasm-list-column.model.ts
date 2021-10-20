import { GermplasmListColumnCategory } from './germplasm-list-column-category.type';

export class GermplasmListColumn {
    constructor(public id: number,
                public name: string,
                public category: GermplasmListColumnCategory,
                public selected: boolean,
                public alias?: string,
                public typeId?: number) {
    }

}
