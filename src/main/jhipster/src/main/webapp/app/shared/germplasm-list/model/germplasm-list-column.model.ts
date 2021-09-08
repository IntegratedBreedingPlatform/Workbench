export class GermplasmListColumn {
    constructor(public id: number,
                public name: string,
                public columnType: GermplasmListColumnType,
                public typeId?: number) {
    }

}

export enum GermplasmListColumnType {
    STATIC = 'STATIC',
    NAMES = 'NAMES',
    VARIABLE = 'VARIABLE'
}
