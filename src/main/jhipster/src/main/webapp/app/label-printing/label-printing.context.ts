import { Injectable } from '@angular/core';

@Injectable()
export class LabelPrintingContext {
    programId: string;
    studyId: number;
    datasetId: number;
    printingLabelType: number;
}
