import { Injectable } from '@angular/core';

@Injectable()
export class LabelPrintingContext {
    studyId: number;
    datasetId: number;
    printingLabelType: number;
}
