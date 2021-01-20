export class LotImportRequest {
    constructor(
        public lotList: LotImportRequestLotList[],
        public stockIdPrefix?: string
    ) {
    }
}

export class LotImportRequestLotList {
    constructor(
        public gid: number,
        public storageLocationAbbr: string,
        public unitName: string,
        public initialBalance: string,
        public stockId: string,
        public notes?: string
    ) {
    }
}
