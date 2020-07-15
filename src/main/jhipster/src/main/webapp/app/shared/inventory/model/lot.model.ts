import { BaseEntity } from '../../model/base-entity';

export class Lot implements BaseEntity {
    constructor(
        public id?: number,
        public lotId?: number,
        public lotUUID?: string,
        public stockId?: string,
        public gid?: number,
        public mgid?: number,
        public designation?: string,
        public status?: string,
        public locationId?: number,
        public locationName?: string,
        public unitId?: number,
        public unitName?: string,
        public actualBalance?: number,
        public availableBalance?: number,
        public reservedTotal?: number,
        public withdrawalTotal?: number,
        public notes?: string,
        public createdByUsername?: string,
        public createdDate?: string,
        public lastDepositDate?: string,
        public lastWithdrawalDate?: string,
        public pendingDepositsTotal?: number
    ) {
    }
}
