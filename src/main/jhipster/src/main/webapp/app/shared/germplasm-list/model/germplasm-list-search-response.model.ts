export class GermplasmListSearchResponse {
    constructor(public listId: number,
                public listName: string,
                public description: string,
                public listOwner: string,
                public listType: string,
                public numberOfEntries: number,
                public status: string,
                public listDate: string,
                public parentFolderName: string,
                public notes?: string) {
    }
}
