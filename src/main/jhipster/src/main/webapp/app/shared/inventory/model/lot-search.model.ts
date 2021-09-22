export class LotSearch {
    constructor(
        public status?: number,
        public stockID?: string,
        public createdDateFrom?: string,
        public createdDateTo?: string,
        public lastDepositDateFrom?: string,
        public lastDepositDateTo?: string,
        public lastWithdrawalDateFrom?: string,
        public lastWithdrawalDateTo?: string,
        public lotIds?: string[],
        public lotUUIDs?: string[],
        public gids?: string[],
        public germplasmListIds?: string,
        public mgids?: string[],
        public locationNameContainsString?: string,
        public designation?: string,
        public notesContainsString?: string,
        public createdByUsername?: string,
        public minActualBalance?: number,
        public maxActualBalance?: number,
        public minAvailableBalance?: number,
        public maxAvailableBalance?: number,
        public minReservedTotal?: number,
        public maxReservedTotal?: number,
        public minWithdrawalTotal?: number,
        public maxWithdrawalTotal?: number,
        public minPendingDepositsTotal?: number,
        public maxPendingDepositsTotal?: number,
        public unitIds?: number[]

    ) {
    }
}
