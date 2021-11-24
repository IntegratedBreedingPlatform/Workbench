export class GermplasmListMetadataRequest {
    constructor(public name: string,
                public description: string,
                public type: string,
                public date: string,
                public notes: string,
                public parentFolderId: string) {
    }
}