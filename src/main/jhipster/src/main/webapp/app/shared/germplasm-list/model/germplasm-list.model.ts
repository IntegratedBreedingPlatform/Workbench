export class GermplasmList {
    constructor(public listId: number,
                public listName: string,
                public creationDate: string,
                public description: string,
                public programUUID: string,
                public locked: boolean) {
    }
}
