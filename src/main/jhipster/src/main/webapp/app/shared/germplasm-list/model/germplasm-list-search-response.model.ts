import { GermplasmListModel } from './germplasm-list.model';

export class GermplasmListSearchResponse extends GermplasmListModel {
    constructor(public listId: number,
                public listName: string,
                public creationDate: string,
                public description: string,
                public programUUID: string,
                public locked: boolean,
                public ownerId: number,
                public listOwner: string,
                public listType: string,
                public numberOfEntries: number,
                public parentFolderName: string,
                public notes?: string) {
        super(listId, listName, creationDate, description, programUUID, locked, ownerId, listType, notes);
    }
}
