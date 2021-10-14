export class GermplasmMergeSummary {
    constructor(
        public countGermplasmToDelete?: number,
        public countListsToUpdate?: number,
        public countStudiesToUpdate?: number,
        public countPlotsToUpdate?: number,
        public countLotsToMigrate?: number,
        public countLotsToClose?: number
    ) {
    }
}
