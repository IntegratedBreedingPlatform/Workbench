export class GermplasmListSearchRequest {
    constructor(public listNameFilter?: any,
                public parentFolderName?: string,
                public ownerName?: string,
                public listOwner?: string,
                public listTypes?: Array<number>,
                public locked?: boolean,
                public numberOfEntriesFrom?: number,
                public numberOfEntriesTo?: number,
                public notes?: string,
                public listDateFrom?: number,
                public listDateTo?: number) {
    }
}
