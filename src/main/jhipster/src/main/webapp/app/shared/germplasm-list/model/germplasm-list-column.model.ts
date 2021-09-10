export class GermplasmListColumn {
    constructor(public id: number,
                public name: string,
                public category: GermplasmListColumnCategory,
                public typeId?: number,
                // TODO: this value should be required once we are saved the customize view
                public selected?: boolean) {
    }

}

export enum GermplasmListColumnCategory {
    STATIC = 'STATIC',
    NAMES = 'NAMES',
    VARIABLE = 'VARIABLE'
}
