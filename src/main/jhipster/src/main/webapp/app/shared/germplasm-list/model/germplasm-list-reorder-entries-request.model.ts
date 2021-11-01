export class GermplasmListReorderEntriesRequestModel {
    constructor(public selectedEntries: number[],
                public entryNumberPosition?: number,
                public endOfList?: boolean) {
    }
}
