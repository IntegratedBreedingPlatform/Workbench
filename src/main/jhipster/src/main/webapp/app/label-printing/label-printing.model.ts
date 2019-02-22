export class LabelPrintingData {
    constructor(
        public studyId?: number,
        public study?: string,
        public fieldMapInfo?: string,
        public fieldMapInfoList?: any,
        public name?: string,
        public owner?: string,
        public description?: string,
        public type?: string,
        public date?: string,
        public numberOfEntries?: number,
        public numberOfLotsWithReservations?: number,
        public numberOfCopies?: number,
        public sorting?: string,
        public title?: string,
        public objective?: string,
        public numberOfInstances?: number,
        public totalNumberOfLabelToPrint?: number,
        public sizeOfLabelSheet?: string,
        public numberOfLabelPerRow?: number,
        public numberOfRowsPerPageOfLabel?: number,
        public leftSelectedLabelFields?: string,
        public rightSelectedLabelFields?: string,
        public mainSelectedLabelFields?: string,
        public generateType?: string,
        public order?: string,
        public filenameWithExtension?: string,
        public filenameDLLocation?: string,
        public isFieldMapsExisting?: string,
        public settingsName?: string,
        public includeColumnHeadinginNonPdf?: string,
        public isStockList?: string,
        public stockListId?: string,
        public stockListTypeName?: string,
        public inventoryDetailsList?: string,
        public barcodeGeneratedAutomatically: boolean = true,
        public firstBarcodeField: number = 0,
        public secondBarcodeField: number = 0,
        public thirdBarcodeField: number = 0,
        public filename = '',
        public barcodeNeeded: boolean = false
    ) {
    }
}

export class LabelsNeededSummary {
    constructor(
        public headers?: any[],
        public values?: any[],
        public totalNumberOfLabelsNeeded?: number
    ) { }
}

export class LabelType {
    constructor(
        public title?: string,
        public key?: string,
        public fields?: { id: number, name: string }[]
    ) { }
}

export class OriginResourceMetadata {
    constructor(
        public defaultFileName?: string,
        public metadata?: Map<string, string>
    ) { }
}

export class PresetSetting {
    constructor(
        public  id?: number,
        public  programUUID?: string,
        public  toolId?: number,
        public  toolSection?: string,
        public  name?: string,
        public  type?: string,
        public  selectedFields?: any[],
        public barcodeSetting?: BarcodeSetting,
        public fileConfiguration?: FileConfiguration,
    ) {
    }
}

export class BarcodeSetting {
    constructor(
        public automaticBarcode?: boolean,
        public barcodeFields?: any[],
        public barcodeNeeded?: boolean
    ) {
    }
}


export class FileConfiguration {
    constructor(
        public outputType?: string
    ) {
    }
}